package com.digdes.school;

public class Main
{
    public static void main(String[] args)
    {
        JavaSchoolStarter jss = new JavaSchoolStarter();

        try
        {
            jss.execute("INSERT VALUES 'lastname' = 'JavaSchoolStarter'");
            System.out.println(jss.execute("SELECT"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
