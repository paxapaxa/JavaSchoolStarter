package com.digdes.school;

public class Main
{
    public static void main(String[] args)
    {
        JavaSchoolStarter jss = new JavaSchoolStarter();

        try
        {
            testINSERT(jss);
            testUPDATE(jss);
            testSELECT(jss);
            testDELETE(jss);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void testINSERT(JavaSchoolStarter jss) throws Exception
    {
        System.out.println("TEST INSERT");
        //normal request
        System.out.println(jss.execute("INSERT VALUES 'lastname'='name1', 'age'=23, 'cost' = 7.6, 'id'=1, 'cost'=5, 'active'=true"));
        //request with extra spaces
        System.out.println(jss.execute("  INSERT   VALUES   'lastname'  =  'name2'  ,  'age'=19  ,   'id'=2  , 'cost' = 567,  'active'=false  "));
        //request with different keyword and cells names case
        System.out.println(jss.execute("iNSERt values 'lasTname'='name3', 'Age'=18, 'cost' = 5.6666, 'ID'=3, 'active'=false"));
        //request with a check for missing duplicate values in cells
        System.out.println(jss.execute(" iNSERt    valUes   'lastname'  = 'name3'  ,  'age'=18 , 'id'=   3, 'cost' = 5.6666,   'active'=false     "));
        //request with null-value cells
        System.out.println(jss.execute("INSERT VALUES 'lastname'=null, 'age'=null, 'id'=9, 'active'=null"));

        //VALIDATION TEST
        try
        {
            //requests with incorrect use of keywords
            //jss.execute("INSERT");
            //jss.execute("INSERT VALUES");
            //jss.execute("INSERT 'lastname'='name1', 'age'=18, 'id'=1, 'active'=true");
            //jss.execute("INSERT fdfgerf VALUES 'lastname'='name1', 'age'=18, 'id'=1, 'active'=true");
            //jss.execute("IN SERT VAL UES 'lastname'='name1', 'age'=18, 'id'=1, 'active'=true");
            //requests with incorrect quotes
            //jss.execute("INSERT VALUES lastname'='name1', 'age=18, 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1, 'age'=18, 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'=18, 'id'='1', 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'='18', 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'=18, 'id'=1, 'active'='true'");
            //requests with incorrect commas
            //jss.execute("INSERT VALUES 'lastname'='name1' 'age'=18, 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'=18, 'id'=1 'active'=true");
            //requests with incorrect values of cells
            //jss.execute("INSERT VALUES 'lastname'=19, 'age'=18, 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'='18', 'id'=1, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'=18, 'id'=fllfdsdg, 'active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'age'=18, 'id'=1, 'active'=tRue");
            //requests with incorrect cell names
            //jss.execute("INSERT VALUES ' lastname'='name1', 'age '=18, 'id'=1, ' active'=true");
            //jss.execute("INSERT VALUES 'lastname'='name1', 'year'=18, 'id'=1, 'active'=true");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void testUPDATE(JavaSchoolStarter jss) throws Exception
    {
        System.out.println("TEST UPDATE");
        //update changes to all rows
//        //WHERE
//        System.out.println(jss.execute(" update      VaLUES 'age' = 18 whERE 'active' = false "));
        //WHERE with AND
        System.out.println(jss.execute("UPDATE VALUES 'id'=2 WHERE 'age'>17"));
//        //WHERE with OR
//        System.out.println(jss.execute("UPDATE VALUES 'id'=3, 'active'=false WHERE 'lastNAME'='name1' OR 'lastname'='name2'"));
//        //WHERE with AND and OR
//        System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age' = 18 AND 'id'=2 Or 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234 "));

        //VALIDATION TEST
        try
        {
            //requests with comparison with null
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age'= null AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234 "));
            //checking the WHERE section
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE age'=17 AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234 "));
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age'=fgdgddgf AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234 "));
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age'=18 AND 'id'=2 OR active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234 "));
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age'=18 AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = fefwer OR 'id'=1234 "));
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age'=18 AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234.4 "));
            //System.out.println(jss.execute("UPDATE VALUES 'id'=1, 'lastNAME'=  'NAME' WHERE 'age' like 18 AND 'id'=2 OR 'active' = true AND 'id'=3 AND 'cost' = 1 OR 'id'=1234.4 "));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void testSELECT(JavaSchoolStarter jss) throws Exception
    {
        System.out.println("TEST SELECT");
//        System.out.println(jss.execute("SELECT WHERE 'lastName' like 'name%'"));
//        System.out.println(jss.execute("SELECT WHERE 'lastname'  like '%N%' and 'age'<=18"));
//        System.out.println(jss.execute("SELECT WHERE 'id'=9"));
        System.out.println(jss.execute("SELECT WHERE 'age'!=3758358 AND 'id'!=234"));
        System.out.println(jss.execute("SELECT"));
    }

    public static void testDELETE(JavaSchoolStarter jss) throws Exception
    {
        System.out.println("TEST DELETE");
        System.out.println(jss.execute("DELETE WHERE 'id' >= 3"));
//        System.out.println(jss.execute("SELECT WHERE 'id'=1"));
//        System.out.println(jss.execute("SELECT"));
//        System.out.println(jss.execute("DELETE"));
        System.out.println(jss.execute("SELECT"));
    }
}
