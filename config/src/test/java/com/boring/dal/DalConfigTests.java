package com.boring.dal;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.model.PackageModel1;
import com.boring.dal.model2.DefinedModel1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DataAccessConfig.class})
public class DalConfigTests {


    @Resource
    private DataAccessConfig dataAccessConfig;

    @Test
    public void loaddataentries() {
        dataAccessConfig.init();
        List<DataEntry> ss = dataAccessConfig.getClassRelatedListInfo(DefinedModel1.class);
        System.out.println(ss);

        DefinedModel1 d = new DefinedModel1();
        d.setApp(2);
        d.setId(1L);
        d.setRcode("rcode1");
        d.setRname("rname1");
        DataEntry skname = dataAccessConfig.getDataEntryByName("DModel_List1");
        System.out.println(skname);

        PackageModel1 p = new PackageModel1();
        p.setId(11L);
        p.setName("pname");
        p.setUris("puris");
        p.setUrl("purl");
        DataEntry skname1 = dataAccessConfig.getDataEntryByName("DModel_PModel_List1");
        System.out.println(skname1);
    }

}
