package com.jiangfendou.mall.product.web;

import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.service.CategoryService;
import com.jiangfendou.mall.product.vo.Catelog2Vo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IndexController.
 * @author jiangmh
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({ "/index.html", "/"})
    public String indexPage(Model model) {
        // 查出一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping({"index/catalog.json"})
    public Map<String, List<Catelog2Vo>> getCatalogJson(Model model) {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {

        // 获取一把锁，只要名字一样，就是同意把锁
        RLock lock = redissonClient.getLock("jiangendou-lock");

        /**
         * 1、锁的自动续期，如果业务超长，运行期间自动给锁续上30秒，不用担心业务时间长，锁自动过期。
         * 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30秒后自动删除。
         * */
        // 阻塞等待，默认加锁的时间是30s
//        lock.lock();
        // 10s自动解锁， 自动解锁的时间一定要大于业务之执行的时间
        // 设置锁的自动解锁时间，就不会自动续期
        lock.lock(30, TimeUnit.SECONDS);
        // 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        // 2、如果我们不指定锁的超时时间，就是用30 * 1000【看门狗的默认时间】，
        // 2、只要占锁成功就会启动一个定时任务，作用就是重新给锁设置新的过期时间，过期时间就是看门狗的默认时间。
        try {
            System.out.println("加锁成功。。。。。" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            System.out.println("释放锁。。。。" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }


    /**
     * redisson 读写锁为了保证一定能读到最新的数据，修改期间写锁是一个排他锁（互斥锁），
     * 读锁是一个共享锁。
     * 但是只要写锁没释放，读就必须等待。
     *
     * 并发读：相当于无锁状态，只会在redis中记录好所有当前的读锁，他们都会加锁成功
     * 写读并发：等待写锁释放
     * 写写并发：阻塞方式
     * 读写并发：有读锁，写就需要等待
     *
     * 只要有写的存在，都必须等待
     * */
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {

        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();

        // 改数据加写锁，读数据加读锁
        rLock.lock();

        String uuid = UUID.randomUUID().toString();
        try {
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("uuid", uuid);
        }catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        rLock.lock();
        String uuid = null;
        try {
            Thread.sleep(300);
            uuid = stringRedisTemplate.opsForValue().get("uuid");
        }catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    /**
     * 放假，锁门
     * 5个班级的人都走了，我们就可以锁门了
     * */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        // 等待闭锁完成
        door.await();

        return "be on holiday!!";

    }


    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 计数减一
        door.countDown();
        return id + ":everyone in the class has left";

    }
}
