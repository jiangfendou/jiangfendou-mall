package com.jiangfendou.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.jiangfendou.common.constant.ProductConstant;
import com.jiangfendou.common.to.SkuHasStockVo;
import com.jiangfendou.common.to.SkuReductionTo;
import com.jiangfendou.common.to.SpuBoundTo;
import com.jiangfendou.common.to.es.SkuEsModel;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.product.entity.AttrEntity;
import com.jiangfendou.mall.product.entity.BrandEntity;
import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.entity.ProductAttrValueEntity;
import com.jiangfendou.mall.product.entity.SkuImagesEntity;
import com.jiangfendou.mall.product.entity.SkuInfoEntity;
import com.jiangfendou.mall.product.entity.SkuSaleAttrValueEntity;
import com.jiangfendou.mall.product.entity.SpuInfoDescEntity;
import com.jiangfendou.mall.product.feign.CouponFeignService;
import com.jiangfendou.mall.product.feign.SearchFeignService;
import com.jiangfendou.mall.product.feign.WareFeignService;
import com.jiangfendou.mall.product.service.AttrService;
import com.jiangfendou.mall.product.service.BrandService;
import com.jiangfendou.mall.product.service.CategoryService;
import com.jiangfendou.mall.product.service.ProductAttrValueService;
import com.jiangfendou.mall.product.service.SkuImagesService;
import com.jiangfendou.mall.product.service.SkuInfoService;
import com.jiangfendou.mall.product.service.SkuSaleAttrValueService;
import com.jiangfendou.mall.product.service.SpuImagesService;
import com.jiangfendou.mall.product.service.SpuInfoDescService;
import com.jiangfendou.mall.product.vo.Attr;
import com.jiangfendou.mall.product.vo.BaseAttrs;
import com.jiangfendou.mall.product.vo.Bounds;
import com.jiangfendou.mall.product.vo.Images;
import com.jiangfendou.mall.product.vo.Skus;
import com.jiangfendou.mall.product.vo.SpuSaveVo;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.SpuInfoDao;
import com.jiangfendou.mall.product.entity.SpuInfoEntity;
import com.jiangfendou.mall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * @author jiangmh
 */
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
            new Query<SpuInfoEntity>().getPage(params),
            wrapper
        );
        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {

        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2、保存spu的描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        this.spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        // 3、保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(images, spuInfoEntity.getId());

        // 4、保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(productAttrValueEntities);

        // 5、保存spu的积分信息 sms_spu_bounds


        // 5、保存当前spu对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image: item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                // 5-1、sku的基本信息 pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    // true 是需要的数据 false 是不需要的数据
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                // 5-2、sku的图片信息 pms_sku_images
                // TODO 没有图片路径的不需要保存到数据库
                skuImagesService.saveBatch(skuImagesEntities);

                // 5-3、sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(attr1 -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr1, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 5-4、sku的优惠、满减等信息 sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_peice
                Bounds bounds = spuSaveVo.getBounds();
                SpuBoundTo spuBoundTo = new SpuBoundTo();
                BeanUtils.copyProperties(bounds, spuBoundTo);
                spuBoundTo.setSpuId(skuInfoEntity.getSkuId());
                R r = couponFeignService.saveSpuBounds(spuBoundTo);
                if (r.getCode() != 0) {
                    log.error("远程保存spu积分信息失败");
                }

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                BeanUtils.copyProperties(item, skuReductionTo);
                if (skuReductionTo.getFullCount() > 0 &&
                    skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存spu优惠信息失败");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public void spuUp(Long spuId) {

        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrs = attrService.selectSearchAttrIds(attrIds);

        Set<Long> idSet = new HashSet<>(searchAttrs);

        List<SkuEsModel.Attrs> attrsList = productAttrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        // 查出当前spuId对应的所有sku信息,品牌名字
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        Map<Long, Boolean> stockMap = null;
        // 远程调用ware系统
        try {
            R skuHasStock =wareFeignService.getSkuHasStock(skuInfoEntities.stream().map(SkuInfoEntity::getSkuId)
                    .collect(Collectors.toList()));
            // *********************** list 集合转成Map
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            stockMap = skuHasStock.getData(typeReference).stream()
                .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch(Exception e) {
            log.error("库存服务查询异常");
        }

        // 封装sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
            // 组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            // 设置库存信息
            if (finalStockMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            // 热度评分 0
            skuEsModel.setHotScore(0L);
            // 商品品牌和分类的名字
            BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            // 设置检索属性
            skuEsModel.setAttrs(attrsList);

            return skuEsModel;
        }).collect(Collectors.toList());

        R result = searchFeignService.productStatusUp(skuEsModels);
        if (result.getCode() == 0) {
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // TODO 接口幂等性
        }
    }
}