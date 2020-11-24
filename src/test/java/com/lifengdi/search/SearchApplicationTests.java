package com.lifengdi.search;

import com.lifengdi.SearchApplication;
import com.lifengdi.docindex.repo.StoreRepository;
import com.lifengdi.document.StoreDocument;
import com.lifengdi.document.store.StoreBaseInfo;
import com.lifengdi.document.store.StoreTags;
import com.lifengdi.init.InitIndexService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class SearchApplicationTests {

    @Resource
    private InitIndexService initIndexService;

    @Resource
    private StoreRepository storeRepository;

    @Test
    public void testInit() {
        // 初始化索引
        initIndexService.initIndex();
    }

    @Test
    public void creatIndex() {
        // 创建索引文档
        StoreDocument document = new StoreDocument();
        document.setId("2");

        StoreBaseInfo storeBaseInfo = new StoreBaseInfo();
        storeBaseInfo.setCreatedTime(DateTime.now());
        storeBaseInfo.setStoreId("1");
        storeBaseInfo.setStoreName("门店1");
        storeBaseInfo.setUpdatedTime(DateTime.now());
        storeBaseInfo.setCreatedTime(DateTime.now());
        document.setBaseInfo(storeBaseInfo);

        StoreTags storeTags = new StoreTags();
        storeTags.setKey("testTag1");
        storeTags.setShowName("测试标签1");
        storeTags.setValue("tags1");

        StoreTags storeTags2 = new StoreTags();
        storeTags2.setKey("testTag2");
        storeTags2.setShowName("测试标签2");
        storeTags2.setValue("tags2");
        document.setTags(Arrays.asList(storeTags, storeTags2));
        storeRepository.index(document);
    }

}
