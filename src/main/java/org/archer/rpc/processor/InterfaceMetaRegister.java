package org.archer.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.archer.rpc.MultiRegistrar;
import org.archer.rpc.constants.Delimiters;
import org.archer.rpc.meta.InterfaceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InterfaceMetaRegister implements MultiRegistrar<String, InterfaceMetaData>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceMetaRegister.class);


    private CuratorFramework curatorClient;

    private TreeCache treeCache;

    private boolean running = false;

    private static final String ROOT_PATH = "services/provider";

    @Value("${zk.connect.string:localhost:2181}")
    private String zkConnectString;

    private static final int DEFAULT_SESSION_TIME_OUT = 5000;

    private static final int DEFAULT_RETRY_INTERVAL = 3000;

    private static final int DEFAULT_MAX_RETRY_TIMES = 3;


    @Override
    public boolean contains(String s) {
        return CollectionUtils.isEmpty(treeCache.getCurrentChildren(polish(s)));
    }


    @Override
    public boolean register(String s, List<InterfaceMetaData> instance) {

        String opResult = null;
        try {
            List<CuratorOp> curatorOps = Lists.newArrayList();
            for (InterfaceMetaData metaData : instance) {
                String path = polish(s) + polish(DigestUtils.md5Hex(JSON.toJSONString(metaData)));
                curatorOps.add(curatorClient.transactionOp().create().withMode(CreateMode.EPHEMERAL).forPath(path, JSON.toJSONString(metaData).getBytes()));
            }
            List<CuratorTransactionResult> results = curatorClient.transaction().forOperations(curatorOps);
            results.forEach(result -> logger.debug(result.getForPath() + Delimiters.COLON + result.getError() + result.getType()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<InterfaceMetaData> remove(String s) {
        try {
            String path = polish(s);
            curatorClient.delete().forPath(path);
            return treeCache
                    .getCurrentChildren(path)
                    .values()
                    .stream()
                    .map(childData -> JSON.parseObject(new String(childData.getData(), StandardCharsets.UTF_8), InterfaceMetaData.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<InterfaceMetaData> get(String s) {
        String path = polish(s);
        return treeCache
                .getCurrentChildren(path)
                .values()
                .stream()
                .map(childData -> JSON.parseObject(new String(childData.getData(), StandardCharsets.UTF_8), InterfaceMetaData.class))
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> ids() {
        return treeCache
                .getCurrentChildren(Delimiters.SLASH)
                .keySet();
    }

    @Override
    public List<List<InterfaceMetaData>> instances() {
        throw new UnsupportedOperationException("not recommend");
    }

    @Override
    public int size() {
        return treeCache.getCurrentChildren(Delimiters.SLASH).size();
    }

    private String polish(String rawKey) {
        return Delimiters.SLASH + rawKey;
    }

    @PreDestroy
    public void stop() {
        running = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initCuratorClient();
        initTreeCache();
        running = true;
    }

    private void initCuratorClient() {
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .sessionTimeoutMs(DEFAULT_SESSION_TIME_OUT)
                .retryPolicy(
                new RetryNTimes(DEFAULT_MAX_RETRY_TIMES, DEFAULT_RETRY_INTERVAL))
                .namespace(ROOT_PATH)
                .build();
        curatorClient.start();
    }

    @SneakyThrows
    private void initTreeCache() {
        treeCache = TreeCache
                .newBuilder(curatorClient, Delimiters.SLASH)
                .setCacheData(true)
                .setCreateParentNodes(false)
                .setDataIsCompressed(false)
                .setMaxDepth(3)
                .build();
        treeCache.start();
    }

    @Override
    public boolean registerInstance(String s, InterfaceMetaData instance) {
        String jsonView = JSON.toJSONString(instance);
        String path = polish(s) + polish(DigestUtils.md5Hex(jsonView));
        try {
            String result = curatorClient.
                    create().
                    creatingParentsIfNeeded().
                    withMode(CreateMode.EPHEMERAL).
                    forPath(path, jsonView.getBytes());
            logger.debug(result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeInstance(String s, InterfaceMetaData oldInstance) {
        String path = polish(s) +
                polish(DigestUtils.md5Hex(JSON.toJSONString(oldInstance)));
        try {
            curatorClient.delete().forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
