package com.orientechnologies.orient.core.serialization.serializer;

import static com.orientechnologies.orient.core.serialization.serializer.OStringSerializerHelper.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import com.orientechnologies.common.io.OIOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OStringSerializerHelperTest {

    @Test
    public void test(){
        final List<String> stringItems = new ArrayList<String>();
        final String text = "['f\\\'oo', 'don\\\'t can\\\'t', \"\\\"bar\\\"\", 'b\\\"a\\\'z', \"q\\\"u\\'x\"]";
        final int startPos = 0;

        OStringSerializerHelper.getCollection(text, startPos, stringItems,
                OStringSerializerHelper.LIST_BEGIN, OStringSerializerHelper.LIST_END, OStringSerializerHelper.COLLECTION_SEPARATOR);

        assertEquals(OIOUtils.getStringContent(stringItems.get(0)), "f'oo");
        assertEquals(OIOUtils.getStringContent(stringItems.get(1)), "don't can't");
        assertEquals(OIOUtils.getStringContent(stringItems.get(2)), "\"bar\"");
        assertEquals(OIOUtils.getStringContent(stringItems.get(3)), "b\"a\'z");
        assertEquals(OIOUtils.getStringContent(stringItems.get(4)), "q\"u\'x");
    }
    
    @Test
    public void testSmartTrim()
    {
    	String input = "   test   ";
    	assertEquals(smartTrim(input, true, true), "test");
    	assertEquals(smartTrim(input, false, true), " test");
    	assertEquals(smartTrim(input, true, false), "test ");
    	assertEquals(smartTrim(input, false, false), " test ");
    }
    
    @Test
    public void testEncode()
    {
    	assertEquals(encode("test"), "test");
    	assertEquals(encode("\"test\""), "\\\"test\\\"");
    	assertEquals(encode("\\test\\"), "\\\\test\\\\");
    	assertEquals(encode("test\"test"), "test\\\"test");
    	assertEquals(encode("test\\test"), "test\\\\test");
    }
    
    @Test
    public void testDecode()
    {
    	assertEquals(decode("test"), "test");
    	assertEquals(decode("\\\"test\\\""),  "\"test\"");
    	assertEquals(decode("\\\\test\\\\"), "\\test\\");
    	assertEquals(decode("test\\\"test"), "test\"test");
    	assertEquals(decode("test\\\\test"), "test\\test");
    }
    
    @Test
    public void testEncodeAndDecode()
    {
    	String[] values = {"test", "test\"", "test\"test", "test\\test", "test\\\\test", "test\\\\\"test", "\\\\\\\\", "\"\"\"\"", "\\\"\\\"\\\""};
    	for (String value : values)
		{
    		String encoded = encode(value);
    		String decoded = decode(encoded);
    		assertEquals(decoded, value);
		}
    }
    
    @Test
    public void testGetMap()
    {
    	String testText = "";
    	Map<String, String> map = OStringSerializerHelper.getMap(testText);
    	assertNotNull(map);
    	assertTrue(map.isEmpty());
    	
    	testText = "{ param1 :value1, param2 :value2}";
    	//testText = "{\"param1\":\"value1\",\"param2\":\"value2\"}";
    	map = OStringSerializerHelper.getMap(testText);
    	assertNotNull(map);
    	assertFalse(map.isEmpty());
    	System.out.println(map);
    	System.out.println(map.keySet());
    	System.out.println(map.values());
    	assertEquals(map.get("param1"), "value1");
    	assertEquals(map.get("param2"), "value2");
    	//Following tests will be nice to support, but currently it's not supported!
    	// {param1 :value1, param2 :value2}
    	// {param1 : value1, param2 : value2}
    	// {param1 : "value1", param2 : "value2"}
    	// {"param1" : "value1", "param2" : "value2"}
    	// {param1 : "value1\\value1", param2 : "value2\\value2"}
    }
    
    @Test
    public void testIndexOf()
    {
    	String testString = "This is my test string";
    	assertEquals(indexOf(testString, 0, 'T'), 0);
    	assertEquals(indexOf(testString, 0, 'h'), 1);
    	assertEquals(indexOf(testString, 0, 'i'), 2);
    	assertEquals(indexOf(testString, 0, 'h', 'i'), 1);
    	assertEquals(indexOf(testString, 2, 'i'), 2);
    	assertEquals(indexOf(testString, 3, 'i'), 5);
    }
    
    @Test
    public void testSmartSplit()
    {
    	String testString = "a, b, c, d";
    	List<String> splitted = smartSplit(testString, ',');
    	assertEquals(splitted.get(0), "a");
    	assertEquals(splitted.get(1), " b");
    	assertEquals(splitted.get(2), " c");
    	assertEquals(splitted.get(3), " d");
    	
    	splitted = smartSplit(testString, ',', ' ');
    	assertEquals(splitted.get(0), "a");
    	assertEquals(splitted.get(1), "b");
    	assertEquals(splitted.get(2), "c");
    	assertEquals(splitted.get(3), "d");
    	
    	splitted = smartSplit(testString, ',', ' ', 'c');
    	assertEquals(splitted.get(0), "a");
    	assertEquals(splitted.get(1), "b");
    	assertEquals(splitted.get(2), "");
    	assertEquals(splitted.get(3), "d");
    	
    	testString = "a test, b test, c test, d test";
    	splitted = smartSplit(testString, ',', ' ');
    	assertEquals(splitted.get(0), "atest");
    	assertEquals(splitted.get(1), "btest");
    	assertEquals(splitted.get(2), "ctest");
    	assertEquals(splitted.get(3), "dtest");
    }
    
}
