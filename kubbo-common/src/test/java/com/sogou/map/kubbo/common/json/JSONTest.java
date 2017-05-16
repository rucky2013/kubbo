/**
 * 
 */
package com.sogou.map.kubbo.common.json;

import org.junit.Assert;
import org.junit.Test;

import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;


/**
 * @author liufuliang
 *
 */
public class JSONTest {
    @Test
    public void testJSONParse() throws JSONException{
        JSONObject obj = new JSONObject("{\"uid\": \"1000443103746\",\"caption\": \"清华大学\",\"category\": \"学校科研\",\"subCategory\": \"知名大学\",\"alias\": \"清华,清华大学本部,清华总部,清华总校区,清华大学校区,THU校区,清华本校区,清华大学本校区,清华大学主校区,清华大学总部,清华大学总校区,清华校区,THU,清华本部,清华主校区,清华学校,国立清华大学,西南联合大学\",\"phone\": \"010-62782165;010-62785001\",\"address\": \"北京市海淀区双清路30号\",\"poiDesc\": \"\",\"province\": \"北京市\",\"city\": \"北京市\",\"county\": \"海淀区\",\"geometry\": {\"type\": \"POINT\",\"coordinate\": [12949561,4837546]}}");
        Assert.assertEquals(1000443103746L, obj.getLong("uid"));
        Assert.assertEquals("清华大学", obj.getString("caption"));
        Assert.assertEquals("[12949561,4837546]", obj.getJSONObject("geometry").getJSONArray("coordinate").toString());
    }
    
    @Test
    public void testCDL() throws JSONException{
        String[] values = {"1", "2", "3"};
        JSONArray arrayObj = new JSONArray(values);
        Assert.assertEquals("1,2,3\n", CDL.rowToString(arrayObj));
    }
    
    @Test
    public void testBean() throws JSONException{
        Bean bean = new Bean();
        bean.setIntValue(1);
        bean.setStringValue("value");
        bean.setBooleanValue(true);
        JSONObject obj = new JSONObject(bean);
        Assert.assertEquals(1, obj.optInt("intValue"));
        
        bean = obj.toBean(Bean.class);
        Assert.assertEquals(1, bean.getIntValue());
        Assert.assertEquals("value", bean.getStringValue());
        Assert.assertEquals(true, bean.booleanValue);
    }

}
