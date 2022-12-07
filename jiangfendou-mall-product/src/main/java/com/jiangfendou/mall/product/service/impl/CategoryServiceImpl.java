package com.jiangfendou.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jiangfendou.mall.product.service.CategoryBrandRelationService;
import com.jiangfendou.mall.product.vo.Catelog2Vo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.CategoryDao;
import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
            new Query<CategoryEntity>().getPage(params),
            new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listTree() {
        // 1、查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 2、组装成父子的树形结构
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return new ArrayList<>();
        }
        // 2-1、找到所有的1级分类
        List<CategoryEntity> levelFirstMenus = categoryEntities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map(menu-> {
            menu.setChildren(getChildren(menu, categoryEntities));
            return menu;
        }).sorted((menuStart, menuEnd) -> {
            return (menuStart.getSort() == null ? 0 : menuStart.getSort()) -
                (menuEnd.getSort() == null ? 0 : menuEnd.getSort());
        }).collect(Collectors.toList());
        return levelFirstMenus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] getCateLogPath(Long cateLogId) throws Exception {
        List<Long> paths = new ArrayList();
        List<Long> parentPath = getParentPath(cateLogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * @CacheEvict 失效模式
     * 1、同时进行多个缓存操作 @Caching
     * 2、指定删除某个分区下的所有数据，
     *      @Caching(evict = {
     *         @CacheEvict(value = "category", key = "'getLevel1Categories'"),
     *         @CacheEvict(value = "category", key = "'getCatalogJson'"),
     *     })
     * 3、存储同一类型的数据，都可以指定同一个分区，分区名默认就是缓存的前缀
     *
     *  @CachePut
     *  双写模式
     * */
//    @CacheEvict(value = "category", key = "'getLevel1Categories'")
//    @Caching(evict = {
//        @CacheEvict(value = "category", key = "'getLevel1Categories'"),
//        @CacheEvict(value = "category", key = "'getCatalogJson'"),
//    })

    // 双写模式
//    @CachePut

    @CacheEvict(value = "category", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCategoryInfo(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * @Cacheable 开启redis注解
     *
     * 如果缓存中有方法就不用调用了，如果缓存中没有，就会调用法方，最后将方法中的结果防弹redis中
     * 每一个需要缓存的数据我们都需要指定要放到哪一个名字的缓存。【缓存的分区：按照业务类型分】
     *
     * 默认行为：
     * 1、如果缓存中有方法就不用调用了
     * 2、key 是自动默认生成的 缓存的名字: :SimpleKey []生成的key值)
     * 3、缓存的view值，默认使用jdk序列化的机制，将序列化后的数据存到redis
     * 4、数据的默认过期时间  是 -1  永不过期
     *
     * 自定义操作
     * 1、key的生成：使用key属性指定，接受一个SpEL
     *     SpEL详细语法： https://docs.spring.io/spring-framework/docs/6.0.3-SNAPSHOT/reference/html/integration.html#cache
     * 2、指定缓存的数据的过期时间：在配置文件中修改 ttl
     * 3、将数据保存为json格式
     *      CacheAutoConfiguration
     *      RedisAutoConfiguration -> 自动配置了RedisCacheManager -> 初始化所有的缓存
     *      -> 每个缓存决定使用什么配置 -> 如果redisCacheConfiguration有就用已有的，没有就默认配置
     *      -> 想修改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可
     *      ->就会应用到当前RedisCacheManager管理的所有缓存分区中
     *
     *
     * */
    @Cacheable(value={"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
            .eq("parent_cid", 0));

        return categoryEntities;
    }

    @Cacheable(value={"category"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Date date = new Date();
        long startTime = date.getTime();
        // 1、查出所有一级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();

        // 获取所有的category1 ids
        List<Long> category1Ids = level1Categories.stream().map(CategoryEntity::getCatId).collect(Collectors.toList());

        List<CategoryEntity> level2Categories =
            baseMapper.selectList(new QueryWrapper<CategoryEntity>().in("parent_cid", category1Ids));

        // 获取所有的category2 ids
        List<Long> category2Ids = level2Categories.stream().map(CategoryEntity::getCatId)
            .collect(Collectors.toList());

        List<CategoryEntity> level3Categories =
            baseMapper.selectList(new QueryWrapper<CategoryEntity>().in("parent_cid", category2Ids));
        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Categories.stream().map(item -> {
            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
            catelog3Vo.setCatalog2Id(item.getParentCid().toString());
            catelog3Vo.setId(item.getCatId().toString());
            catelog3Vo.setName(item.getName());
            return catelog3Vo;
        }).collect(Collectors.toList());

        // 将 category2 和 category3组合
        Map<String, List<Catelog2Vo.Catelog3Vo>> map =
            catelog3Vos.stream().collect(Collectors.groupingBy(Catelog2Vo.Catelog3Vo::getCatalog2Id));

        List<Catelog2Vo> catelog2Vos = level2Categories.stream().map(item -> {
            Catelog2Vo catelog2Vo = new Catelog2Vo();
            catelog2Vo.setCatalog3List(map.get(item.getCatId().toString()));
            catelog2Vo.setId(item.getCatId().toString());
            catelog2Vo.setCatalogId(item.getParentCid().toString());
            catelog2Vo.setName(item.getName());
            return catelog2Vo;
        }).collect(Collectors.toList());

        // 将 category1 和 category2组合
        Map<String, List<Catelog2Vo>> catelog2Map =
            catelog2Vos.stream().collect(Collectors.groupingBy(Catelog2Vo::getCatalogId));

        Date date1 = new Date();
        long endTime = date1.getTime();
        log.info("查询了数据库~~~~~~~~~~~~~~~~");
        log.info("花费时间：{}", endTime - startTime);
        return catelog2Map;
    }


    public Map<String, List<Catelog2Vo>> getCatalogJson2() {

        /**
         * 1、缓存击穿   空结果缓存
         * 2、设置过期时间（加随机值） 解决缓存雪崩的问题
         * 3、加锁：解决缓存击穿的问题
         */

        String catelogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catelogJson)) {
            log.info("get the data from redis {}", catelogJson);
            Map<String, List<Catelog2Vo>> map = JSON.parseObject(catelogJson,
                new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return map;
        }
        Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromWithRedisLock();
        return catalogJsonFromDB;
    }

    /**
     * 缓存的数据如何与数据库保持一直
     * 缓存数据一致性
     * 1、双写模式
     * 2、失效模式
     * */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromWithRedissonLock() {

        RLock catalogJsonLock = redissonClient.getLock("CatalogJson-lock");
        catalogJsonLock.lock();



        Map<String, List<Catelog2Vo>> data;
        try {
            data = getData();
        } finally {
            catalogJsonLock.unlock();
        }
        return data;
    }

    /**
     * 从数据库查询并封装数据
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromWithRedisLock() {

        // 占用分布式锁
        String uuid = UUID.randomUUID().toString();
        // 加锁使用  set nx ex 命令
        // 保证加锁和设置过期时间的原子性
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,
            300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功。。。。");
            // 加锁成功
            // 设置过期时间，解决死锁的问题
//            stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> data;
            try {
                data = getData();
            } finally {
                // 删除锁, 查询和删除锁 必须事原子操作，使用lua脚本解锁 保证查询和删除锁的原子性
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end";
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                    Arrays.asList("lock"), uuid);
            }
//            stringRedisTemplate.delete("lock");
//            String redisLock = stringRedisTemplate.opsForValue().get("lock");
//            if (Objects.equals(uuid, redisLock)) {
//                stringRedisTemplate.delete("lock");
//            }

            return data;
        } else {
            // 加锁失败。。。。。重试
            // 休眠 100 毫秒
            try {
                Thread.sleep(200);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromWithRedisLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getData () {
        // synchronized为本地锁，只能锁住当前进程的，在分布式的情况下  想要锁住所有请求
        String catelogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catelogJson)) {
            Map<String, List<Catelog2Vo>> map = JSON.parseObject(catelogJson,
                new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return map;
        }
        Date date = new Date();
        long startTime = date.getTime();
        // 1、查出所有一级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();

        // 获取所有的category1 ids
        List<Long> category1Ids = level1Categories.stream().map(CategoryEntity::getCatId).collect(Collectors.toList());

        List<CategoryEntity> level2Categories =
            baseMapper.selectList(new QueryWrapper<CategoryEntity>().in("parent_cid", category1Ids));

        // 获取所有的category2 ids
        List<Long> category2Ids = level2Categories.stream().map(CategoryEntity::getCatId)
            .collect(Collectors.toList());

        List<CategoryEntity> level3Categories =
            baseMapper.selectList(new QueryWrapper<CategoryEntity>().in("parent_cid", category2Ids));
        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Categories.stream().map(item -> {
            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
            catelog3Vo.setCatalog2Id(item.getParentCid().toString());
            catelog3Vo.setId(item.getCatId().toString());
            catelog3Vo.setName(item.getName());
            return catelog3Vo;
        }).collect(Collectors.toList());

        // 将 category2 和 category3组合
        Map<String, List<Catelog2Vo.Catelog3Vo>> map =
            catelog3Vos.stream().collect(Collectors.groupingBy(Catelog2Vo.Catelog3Vo::getCatalog2Id));

        List<Catelog2Vo> catelog2Vos = level2Categories.stream().map(item -> {
            Catelog2Vo catelog2Vo = new Catelog2Vo();
            catelog2Vo.setCatalog3List(map.get(item.getCatId().toString()));
            catelog2Vo.setId(item.getCatId().toString());
            catelog2Vo.setCatalogId(item.getParentCid().toString());
            catelog2Vo.setName(item.getName());
            return catelog2Vo;
        }).collect(Collectors.toList());

        // 将 category1 和 category2组合
        Map<String, List<Catelog2Vo>> catelog2Map =
            catelog2Vos.stream().collect(Collectors.groupingBy(Catelog2Vo::getCatalogId));

        stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(catelog2Map),
            1, TimeUnit.DAYS);
        Date date1 = new Date();
        long endTime = date1.getTime();
        log.info("查询了数据库~~~~~~~~~~~~~~~~");
        log.info("花费时间：{}", endTime - startTime);
        return catelog2Map;
    }

    // 从数据库查询并封装数据
    public synchronized Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {
       return getData();
    }

    private List<Long> getParentPath(Long cateLogId, List<Long> paths) throws Exception {
        paths.add(cateLogId);
        CategoryEntity categoryEntity = this.getById(cateLogId);
        if (categoryEntity == null) {
            throw new Exception();
        }
        if (categoryEntity.getParentCid() != 0) {
            getParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归查找所有菜单的子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity categoryEntity, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter(category -> {
            return Objects.equals(category.getParentCid(), categoryEntity.getCatId());
        }).map(categoryCopy -> {
            // 找到子菜单
            categoryCopy.setChildren(getChildren(categoryCopy, categoryEntities));
            return categoryCopy;
            // 菜单的排序
        }).sorted((menuStart, menuEnd) -> {
            return (menuStart.getSort() == null ? 0 : menuStart.getSort()) -
                (menuEnd.getSort() == null ? 0 : menuEnd.getSort());
        }).collect(Collectors.toList());
        return children;
    }
}