package com.digdes.school;

import java.math.BigDecimal;
import java.util.*;

public class JavaSchoolStarter
{
    private final List<Map<String, Object>> table = new ArrayList<>();

    public JavaSchoolStarter()
    {

    }

    public List<Map<String, Object>> execute(String request) throws Exception
    {
        List<Map<String, Object>> elements;
        request = request.trim();

        try
        {
            switch (request.split("\\s+")[0].toUpperCase())
            {
                case "INSERT" -> elements = insert(request);
                case "UPDATE" -> elements = update(request);
                case "DELETE" -> elements = delete(request);
                case "SELECT" -> elements = select(request);
                default -> throw new Exception("there is no such command");
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new Exception("the command is incomplete");
        }

        return elements;
    }

    private List<Map<String, Object>> insert(String request) throws Exception
    {
        if (!request.split("\\s+")[1].equalsIgnoreCase("VALUES"))
        {
            throw new Exception("invalid characters after INSERT");
        }

        Map<String, Object> newRow = new HashMap<>();

        writeValuesToCells(request, newRow);

        if (newRow.size() == 0)
        {
            throw new Exception("all values in a row cannot be empty!");
        }

        table.add(newRow);

        List<Map<String, Object>> element = new ArrayList<>();
        element.add(newRow);

        return element;
    }

    private List<Map<String, Object>> update(String request) throws Exception
    {
        if (!request.split("\\s+")[1].equalsIgnoreCase("VALUES"))
        {
            throw new Exception("invalid characters after UPDATE");
        }

        List<Map<String, Object>> rowsWhere;

        if (request.toUpperCase().contains("WHERE"))
        {
            rowsWhere = selectRowsWhere(request);
        }
        else
        {
            rowsWhere = cloneTable(table);
        }

        for (Map<String, Object> rowWhere : rowsWhere)
        {
            table.removeIf(row -> row.equals(rowWhere));
        }

        for (Map<String, Object> row : rowsWhere)
        {
            writeValuesToCells(request.split("(?i)WHERE")[0], row);

            if (row.size() == 0)
            {
                throw new Exception("all values in a row cannot be empty!");
            }
        }

        table.addAll(rowsWhere);

        return rowsWhere;
    }

    private void writeValuesToCells(String request, Map<String, Object> row) throws Exception
    {
        String valuesSection = request.split("(?i)VALUES")[1];

        for (String cellValue : valuesSection.split(","))
        {
            if (!cellValue.trim().startsWith("'"))
            {
                throw new Exception("invalid characters between cell values");
            }

            switch (cellValue.split("'")[1].toLowerCase())
            {
                case "id" ->
                {
                    if (!cellValue.split("(?i)'id'")[1].trim().startsWith("="))
                    {
                        throw new Exception("invalid characters after column name");
                    }

                    String idValue = cellValue.split("=")[1].replaceAll("\\s", "");

                    if (idValue.equals("null"))
                    {
                        row.remove("id");
                    }
                    else
                    {
                        try
                        {
                            long id = Long.parseLong(idValue);
                            row.put("id", id);
                        }
                        catch (Exception e)
                        {
                            throw new Exception("invalid id value");
                        }
                    }
                }
                case "lastname" ->
                {
                    if (!cellValue.split("(?i)'lastName'")[1].trim().startsWith("="))
                    {
                        throw new Exception("invalid characters after column name");
                    }

                    if (cellValue.split("=")[1].replaceAll("\\s", "").equals("null"))
                    {
                        row.remove("lastName");
                    }
                    else if (cellValue.replaceAll("\\s", "").endsWith("'"))
                    {
                        String lastName = cellValue.split("=")[1].split("'")[1];
                        row.put("lastName", lastName);
                    }
                    else
                    {
                        throw new Exception("invalid lastName value");
                    }
                }
                case "age" ->
                {
                    if (!cellValue.split("(?i)'age'")[1].trim().startsWith("="))
                    {
                        throw new Exception("invalid characters after column name");
                    }

                    String ageValue = cellValue.split("=")[1].replaceAll("\\s", "");

                    if (ageValue.equals("null"))
                    {
                        row.remove("age");
                    }
                    else
                    {
                        try
                        {
                            long age = Long.parseLong(ageValue);
                            row.put("age", age);
                        }
                        catch (Exception e)
                        {
                            throw new Exception("invalid age value");
                        }
                    }
                }
                case "cost" ->
                {
                    if (!cellValue.split("(?i)'cost'")[1].trim().startsWith("="))
                    {
                        throw new Exception("invalid characters after column name");
                    }

                    String costValue = cellValue.split("=")[1].replaceAll("\\s", "");

                    if (costValue.equals("null"))
                    {
                        row.remove("cost");
                    }
                    else
                    {
                        try
                        {
                            double cost = Double.parseDouble(costValue);
                            row.put("cost", cost);
                        }
                        catch (Exception e)
                        {
                            throw new Exception("invalid cost value");
                        }
                    }
                }
                case "active" ->
                {
                    if (!cellValue.split("(?i)'active'")[1].trim().startsWith("="))
                    {
                        throw new Exception("invalid characters after column name");
                    }

                    String activeValue = cellValue.split("=")[1].replaceAll("\\s", "");

                    if (activeValue.equals("null"))
                    {
                        row.remove("active");
                    }
                    else if (activeValue.equals("true") || activeValue.equals("false"))
                    {
                        boolean active = Boolean.parseBoolean(activeValue);
                        row.put("active", active);
                    }
                    else
                    {
                        throw new Exception("invalid active value");
                    }
                }
                default -> throw new Exception("invalid column name");
            }
        }
    }

    private List<Map<String, Object>> selectRowsWhere(String request) throws Exception
    {
        String whereSection = request.split("(?i)WHERE")[1];

        if (!whereSection.trim().startsWith("'"))
        {
            throw new Exception("invalid characters after WHERE");
        }

        if (whereSection.contains("null"))
        {
            throw new Exception("null values cannot be passed for comparison");
        }

        String[] orSections = whereSection.split("(?i)OR");
        List<Map<String, Object>> rowsWhereForOr = new ArrayList<>();

        for (String orSection : orSections)
        {
            if (!orSection.trim().startsWith("'"))
            {
                throw new Exception("invalid characters after OR");
            }

            String[] andSections = orSection.split("(?i)AND");
            List<Map<String, Object>> rowsWhereForAnd = cloneTable(table);

            for (String andSection : andSections)
            {
                if (!andSection.trim().startsWith("'"))
                {
                    throw new Exception("invalid characters after AND");
                }

                switch (andSection.split("'")[1].toLowerCase())
                {
                    case "id" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);

                        if (!andSection.split("(?i)'id'")[1].trim().startsWith(operatorOfCondition))
                        {
                            throw new Exception("invalid characters after column name");
                        }

                        BigDecimal id;

                        try
                        {
                            if (operatorOfCondition.equals("!=") || operatorOfCondition.equals("="))
                            {
                                long longValue = Long.parseLong(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                                id = new BigDecimal(longValue);
                            }
                            else
                            {
                                id = new BigDecimal(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                            }
                        }
                        catch (Exception e)
                        {
                            if (operatorOfCondition.equals("!="))
                            {
                                break;
                            }
                            else if (operatorOfCondition.equals("="))
                            {
                                rowsWhereForAnd.clear();
                                break;
                            }
                            else
                            {
                                throw new Exception("invalid id value");
                            }
                        }

                        Iterator<Map<String, Object>> rowIterator = rowsWhereForAnd.iterator();

                        while (rowIterator.hasNext())
                        {
                            Map<String, Object> nextRow = rowIterator.next();

                            if (!nextRow.containsKey("id"))
                            {
                                if (!operatorOfCondition.equals("!="))
                                {
                                    rowIterator.remove();
                                }

                                break;
                            }

                            for (Map.Entry<String, Object> pair : nextRow.entrySet())
                            {
                                if (pair.getKey().equals("id"))
                                {
                                    if (!checkConditionForNumbers(
                                            BigDecimal.valueOf((long) pair.getValue()), operatorOfCondition, id))
                                    {
                                        rowIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    case "lastname" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);

                        if (!andSection.split("(?i)'lastName'")[1].trim().startsWith(operatorOfCondition))
                        {
                            throw new Exception("invalid characters after column name");
                        }

                        if (andSection.split(operatorOfCondition)[1].trim().startsWith("'") &&
                                andSection.trim().endsWith("'"))
                        {
                            String lastName = andSection.split(operatorOfCondition)[1].split("'")[1];

                            Iterator<Map<String, Object>> rowIterator = rowsWhereForAnd.iterator();

                            while (rowIterator.hasNext())
                            {
                                Map<String, Object> nextRow = rowIterator.next();

                                if (!nextRow.containsKey("lastName"))
                                {
                                    if (!operatorOfCondition.equals("!="))
                                    {
                                        rowIterator.remove();
                                    }

                                    break;
                                }

                                for (Map.Entry<String, Object> pair : nextRow.entrySet())
                                {
                                    if (pair.getKey().equals("lastName"))
                                    {
                                        if (!checkConditionForString((String) pair.getValue(),
                                                operatorOfCondition, lastName))
                                        {
                                            rowIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (operatorOfCondition.equals("="))
                            {
                                rowsWhereForAnd.clear();
                            }
                            else
                            {
                                throw new Exception("invalid lastName value");
                            }
                        }
                    }
                    case "age" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);

                        if (!andSection.split("(?i)'age'")[1].trim().startsWith(operatorOfCondition))
                        {
                            throw new Exception("invalid characters after column name");
                        }

                        BigDecimal age;

                        try
                        {
                            if (operatorOfCondition.equals("!=") || operatorOfCondition.equals("="))
                            {
                                long longValue = Long.parseLong(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                                age = new BigDecimal(longValue);
                            }
                            else
                            {
                                age = new BigDecimal(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                            }
                        }
                        catch (Exception e)
                        {
                            if (operatorOfCondition.equals("!="))
                            {
                                break;
                            }
                            else if (operatorOfCondition.equals("="))
                            {
                                rowsWhereForAnd.clear();
                                break;
                            }
                            else
                            {
                                throw new Exception("invalid age value");
                            }
                        }

                        Iterator<Map<String, Object>> rowIterator = rowsWhereForAnd.iterator();

                        while (rowIterator.hasNext())
                        {
                            Map<String, Object> nextRow = rowIterator.next();

                            if (!nextRow.containsKey("age"))
                            {
                                if (!operatorOfCondition.equals("!="))
                                {
                                    rowIterator.remove();
                                }

                                break;
                            }

                            for (Map.Entry<String, Object> pair : nextRow.entrySet())
                            {
                                if (pair.getKey().equals("age"))
                                {
                                    if (!checkConditionForNumbers(
                                            BigDecimal.valueOf((long) pair.getValue()), operatorOfCondition, age))
                                    {
                                        rowIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    case "cost" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);

                        if (!andSection.split("(?i)'cost'")[1].trim().startsWith(operatorOfCondition))
                        {
                            throw new Exception("invalid characters after column name");
                        }

                        BigDecimal cost;

                        try
                        {
                            cost = new BigDecimal(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));

                            if (cost.toString().split(".")[1].trim().length() == 0)
                            {
                                throw new Exception();
                            }
                        }
                        catch (Exception e)
                        {
                            if (operatorOfCondition.equals("!="))
                            {
                                break;
                            }
                            else if (operatorOfCondition.equals("="))
                            {
                                rowsWhereForAnd.clear();
                                break;
                            }
                            else
                            {
                                throw new Exception("invalid cost value");
                            }
                        }

                        Iterator<Map<String, Object>> rowIterator = rowsWhereForAnd.iterator();

                        while (rowIterator.hasNext())
                        {
                            Map<String, Object> nextRow = rowIterator.next();

                            if (!nextRow.containsKey("cost"))
                            {
                                if (!operatorOfCondition.equals("!="))
                                {
                                    rowIterator.remove();
                                }

                                break;
                            }

                            for (Map.Entry<String, Object> pair : nextRow.entrySet())
                            {
                                if (pair.getKey().equals("cost"))
                                {
                                    if (!checkConditionForNumbers(
                                            BigDecimal.valueOf((double) pair.getValue()), operatorOfCondition, cost))
                                    {
                                        rowIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    case "active" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);
                        String activeValue = andSection.split(operatorOfCondition)[1].replaceAll("\\s", "");

                        if (!andSection.split("(?i)'active'")[1].trim().startsWith(operatorOfCondition))
                        {
                            throw new Exception("invalid characters after column name");
                        }

                        if (activeValue.equals("true") || activeValue.equals("false"))
                        {
                            boolean active = Boolean.parseBoolean(activeValue);

                            Iterator<Map<String, Object>> rowIterator = rowsWhereForAnd.iterator();

                            while (rowIterator.hasNext())
                            {
                                Map<String, Object> nextRow = rowIterator.next();

                                if (!nextRow.containsKey("active"))
                                {
                                    if (!operatorOfCondition.equals("!="))
                                    {
                                        rowIterator.remove();
                                    }

                                    break;
                                }

                                for (Map.Entry<String, Object> pair : nextRow.entrySet())
                                {
                                    if (pair.getKey().equals("active"))
                                    {
                                        if (!checkConditionForBoolean((boolean) pair.getValue(), operatorOfCondition, active))
                                        {
                                            rowIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (operatorOfCondition.equals("="))
                            {
                                rowsWhereForAnd.clear();
                            }
                            else
                            {
                                throw new Exception("invalid active value");
                            }
                        }
                    }
                    default -> throw new Exception("there is no such column");
                }
            }

            for (Map<String, Object> rowWhereForAnd : rowsWhereForAnd)
            {
                if (rowsWhereForOr.isEmpty())
                {
                    rowsWhereForOr.addAll(rowsWhereForAnd);
                }
                else
                {
                    boolean duplicate = false;

                    for (Map<String, Object> rowWhereForOr : rowsWhereForOr)
                    {
                        if (rowWhereForAnd.equals(rowWhereForOr))
                        {
                            duplicate = true;
                            break;
                        }
                    }

                    if (!duplicate)
                    {
                        rowsWhereForOr.add(rowWhereForAnd);
                    }
                }
            }
        }

        return rowsWhereForOr;
    }

    private boolean checkConditionForNumbers(BigDecimal obj1, String condition, BigDecimal obj2) throws Exception
    {
        switch (condition)
        {
            case ">=" -> { return obj1.compareTo(obj2) >= 0; }
            case "<=" -> { return obj1.compareTo(obj2) <= 0; }
            case "!=" -> { return obj1.compareTo(obj2) != 0; }
            case "=" -> { return obj1.compareTo(obj2) == 0; }
            case ">" -> { return obj1.compareTo(obj2) > 0; }
            case "<" -> { return obj1.compareTo(obj2) < 0; }
            default -> throw new Exception("no such condition");
        }
    }

    private boolean checkConditionForBoolean(boolean obj1, String condition, boolean obj2) throws Exception
    {
        switch (condition)
        {
            case "=" -> { return obj1 == obj2; }
            case "!=" -> { return obj1 != obj2; }
            default -> throw new Exception("no such condition");
        }
    }

    private boolean checkConditionForString(String obj1, String condition, String obj2) throws Exception
    {
        switch(condition)
        {
            case "=" -> { return obj1.equals(obj2); }
            case "!=" -> { return !obj1.equals(obj2); }
            case "like" ->
            {
                if (obj2.startsWith("%") && obj2.endsWith("%"))
                {
                    obj2 = obj2.substring(1, obj2.length() - 1);
                    return obj1.contains(obj2);
                }
                else if (obj2.startsWith("%"))
                {
                    obj2 = obj2.substring(1);
                    return obj1.endsWith(obj2);
                }
                else if (obj2.endsWith("%"))
                {
                    obj2 = obj2.substring(0, obj2.length() - 1);
                    return obj1.startsWith(obj2);
                }
                else
                {
                    return obj1.equals(obj2);
                }
            }
            case "ilike" ->
            {
                if (obj2.startsWith("%") && obj2.endsWith("%"))
                {
                    obj2 = obj2.substring(1, obj2.length() - 1);
                    return obj1.toLowerCase().contains(obj2.toLowerCase());
                }
                else if (obj2.startsWith("%"))
                {
                    obj2 = obj2.substring(1);
                    return obj1.toLowerCase().endsWith(obj2.toLowerCase());
                }
                else if (obj2.endsWith("%"))
                {
                    obj2 = obj2.substring(0, obj2.length() - 1);
                    return obj1.toLowerCase().startsWith(obj2.toLowerCase());
                }
                else
                {
                    return obj1.equalsIgnoreCase(obj2);
                }
            }
            default -> throw new Exception("no such condition");
        }
    }

    private String parseOperatorOfCondition(String condition) throws Exception
    {
        if (condition.contains(">="))
        {
            return ">=";
        }
        else if (condition.contains("<="))
        {
            return "<=";
        }
        else if (condition.contains("!="))
        {
            return "!=";
        }
        else if (condition.contains("="))
        {
            return "=";
        }
        else if (condition.contains(">"))
        {
            return ">";
        }
        else if (condition.contains("<"))
        {
            return "<";
        }
        else if (condition.toLowerCase().contains(" like "))
        {
            return "like";
        }
        else if (condition.toLowerCase().contains(" ilike "))
        {
            return "ilike";
        }
        else
        {
            throw new Exception("no such operator of condition");
        }
    }

    private List<Map<String, Object>> cloneTable(List<Map<String, Object>> table)
    {
        List<Map<String, Object>> cloneTable = new ArrayList<>();

        for (Map<String, Object> row : table)
        {
            Map<String, Object> cloneRow = new HashMap<>(row);
            cloneTable.add(cloneRow);
        }

        return cloneTable;
    }

    private List<Map<String, Object>> delete(String request) throws Exception
    {
        if (request.trim().equalsIgnoreCase("DELETE"))
        {
            List<Map<String, Object>> deleteTable = cloneTable(table);
            table.clear();
            return deleteTable;
        }

        if (!request.split("\\s+")[1].equalsIgnoreCase("WHERE"))
        {
            throw new Exception("invalid characters after DELETE");
        }

        List<Map<String, Object>> rowsWhere = selectRowsWhere(request);

        for(Map<String, Object> rowWhere : rowsWhere)
        {
            table.removeIf(row -> row.equals(rowWhere));
        }

        return rowsWhere;
    }

    private List<Map<String, Object>> select(String request) throws Exception
    {
        if (request.trim().equalsIgnoreCase("SELECT"))
        {
            return cloneTable(table);
        }

        if (!request.split("\\s+")[1].equalsIgnoreCase("WHERE"))
        {
            throw new Exception("invalid characters after SELECT");
        }

        return selectRowsWhere(request);
    }
}
