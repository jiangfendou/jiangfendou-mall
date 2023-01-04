package com.jiangfendou.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.jiangfendou.common.exception.NoStockException;
import com.jiangfendou.common.to.OrderTo;
import com.jiangfendou.common.to.SkuHasStockVo;
import com.jiangfendou.common.to.mq.StockDetailTo;
import com.jiangfendou.common.to.mq.StockLockedTo;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.ware.entity.WareOrderTaskDetailEntity;
import com.jiangfendou.mall.ware.entity.WareOrderTaskEntity;
import com.jiangfendou.mall.ware.feign.OrderFeignService;
import com.jiangfendou.mall.ware.feign.ProductFeginService;
import com.jiangfendou.mall.ware.service.WareOrderTaskDetailService;
import com.jiangfendou.mall.ware.service.WareOrderTaskService;
import com.jiangfendou.mall.ware.vo.OrderItemVo;
import com.jiangfendou.mall.ware.vo.OrderVo;
import com.jiangfendou.mall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.ware.dao.WareSkuDao;
import com.jiangfendou.mall.ware.entity.WareSkuEntity;
import com.jiangfendou.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity>
    implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeginService productFeginService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);

        }
        String wareId = (String)params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);

        }
        IPage<WareSkuEntity> page = this.page(
            new Query<WareSkuEntity>().getPage(params),
            wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities =
            wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(wareSkuEntities)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // TODO 远程查询sku的name
            try {
                R info = productFeginService.info(skuId);
                Map<String, Object> map = (Map<String, Object>)info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String)map.get("skuName"));
                }
            } catch (Exception e) {
                log.warn("远程获取sku name失败");
            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            this.wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            Long count = this.baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());

        return skuHasStockVos;
    }

    /**
     * 保存库存工作单详情信息
     * 追溯
     */
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {

        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);


        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map((item) -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪个仓库有库存
            List<Long> wareIdList = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIdList);

            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();

            if (CollectionUtils.isEmpty(wareIds)) {
                // 没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }

            //1、如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存的工作单信息都回滚了。发送出去的消息，即使要解锁库存，由于在数据库查不到指定的id，所有就不用解锁
            for (Long wareId : wareIds) {
                // 锁定成功就返回1，失败就返回0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    WareOrderTaskDetailEntity taskDetailEntity = WareOrderTaskDetailEntity.builder()
                        .skuId(skuId)
                        .skuName("")
                        .skuNum(hasStock.getNum())
                        .taskId(wareOrderTaskEntity.getId())
                        .wareId(wareId)
                        .lockStatus(1)
                        .build();
                    wareOrderTaskDetailService.save(taskDetailEntity);

                    // 告诉MQ库存锁定成功
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity,detailTo);
                    lockedTo.setDetailTo(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }

            if (skuStocked == false) {
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }

        //3、肯定全部都是锁定成功的
        return true;

    }

    @Override
    public void unlockStock(StockLockedTo to) {
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();

        /**
         * 解锁
         * 1、查询数据库关于这个订单锁定库存信息
         *   有：证明库存锁定成功了
         *      解锁：订单状况
         *          1、没有这个订单，必须解锁库存
         *          2、有这个订单，不一定解锁库存
         *              订单状态：已取消：解锁库存
         *                      已支付：不能解锁库存
         */
        WareOrderTaskDetailEntity taskDetailInfo = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailInfo != null) {
            // 查出wms_ware_order_task工作单的信息
            Long id = to.getId();
            WareOrderTaskEntity orderTaskInfo = wareOrderTaskService.getById(id);
            // 获取订单号查询订单状态
            String orderSn = orderTaskInfo.getOrderSn();
            // 远程查询订单信息
            R orderData = orderFeignService.getOrderStatus(orderSn);
            if (orderData.getCode() == 0) {
                // 订单数据返回成功
                OrderVo orderInfo = orderData.getData("data", new TypeReference<OrderVo>() {});

                // 判断订单状态是否已取消或者支付或者订单不存在
                if (orderInfo == null || orderInfo.getStatus() == 4) {
                    // 订单已被取消，才能解锁库存
                    if (taskDetailInfo.getLockStatus() == 1) {
                        // 当前库存工作单详情状态1，已锁定，但是未解锁才可以解锁
                        unLockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                        log.info("库存解锁成功~~~~~~~~~~~~~~~~");
                    }
                } else {
                    log.info("订单状态正常无所解锁库存");
                }
            } else {
                // 消息拒绝以后重新放在队列里面，让别人继续消费解锁
                // 远程调用服务失败
                throw new RuntimeException("远程调用服务失败");
            }
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存优先到期，查订单状态新建，什么都不处理
     * 导致卡顿的订单，永远都不能解锁库存
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockStock(OrderTo orderTo) {

        String orderSn = orderTo.getOrderSn();
        // 查一下最新的库存解锁状态，防止重复解锁库存
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);

        // 按照工作单的id找到所有 没有解锁的库存，进行解锁
        Long id = orderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
            .eq("task_id", id).eq("lock_status", 1));

        for (WareOrderTaskDetailEntity taskDetailEntity : list) {
            unLockStock(taskDetailEntity.getSkuId(),
                taskDetailEntity.getWareId(),
                taskDetailEntity.getSkuNum(),
                taskDetailEntity.getId());
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void test() {
        WareSkuEntity wareSkuEntity = new WareSkuEntity();
        wareSkuEntity.setSkuName("test");
        wareSkuEntity.setWareId(1L);
        wareSkuEntity.setStockLocked(1);
        this.baseMapper.insert(wareSkuEntity);
//        int num = 10/0;
    }

    /**
     * 解锁库存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {

        // 库存解锁
        wareSkuDao.unLockStock(skuId, wareId, num);

        // 更新工作单的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        // 变为已解锁
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }
}