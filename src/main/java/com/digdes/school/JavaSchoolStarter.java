package com.digdes.school;

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
                default -> throw new IllegalArgumentException("there is no such command");
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException ("there is no such command (the command is incomplete)");
        }

        return elements;
    }

    private List<Map<String, Object>> insert(String request) throws ArrayIndexOutOfBoundsException
    {
        if (!(request.split("\\s+")[1].equalsIgnoreCase("VALUES")
                && request.replaceAll("\\s+", "").toUpperCase().startsWith("INSERTVALUES'")))
        {
            throw new IllegalArgumentException("there is no such command");
        }

        Map<String, Object> newRow = new HashMap<>();

        writeValuesToCells(request, newRow);

        if (newRow.size() == 0)
        {
            throw new IllegalArgumentException("all values in a row cannot be empty!");
        }

        table.add(newRow);

        List<Map<String, Object>> element = new ArrayList<>();
        element.add(newRow);

        return element;
    }

    private List<Map<String, Object>> update(String request) throws ArrayIndexOutOfBoundsException
    {
        if (!(request.split("\\s+")[1].equalsIgnoreCase("VALUES") && request.replaceAll("\\s+", "").toUpperCase().startsWith("UPDATEVALUES'")))
        {
            throw new IllegalArgumentException("there is no such command");
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
                throw new IllegalArgumentException("all values in a row cannot be empty!");
            }
        }

        table.addAll(rowsWhere);

        return rowsWhere;
    }

    private void writeValuesToCells(String request, Map<String, Object> row)
    {
        for (String cellValue : request.split(","))
        {
            switch (cellValue.split("'")[1].toLowerCase())
            {
                case "id" ->
                {
                    try
                    {
                        String idValue = cellValue.split("=")[1].replaceAll("\\s", "");

                        if (idValue.equals("null"))
                        {
                            row.remove("id");
                        }
                        else
                        {
                            long id = Long.parseLong(idValue);
                            row.put("id", id);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("invalid id value");
                    }
                }
                case "lastname" ->
                {
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
                        throw new IllegalArgumentException("invalid lastName value");
                    }
                }
                case "age" ->
                {
                    try
                    {
                        String ageValue = cellValue.split("=")[1].replaceAll("\\s", "");

                        if (ageValue.equals("null"))
                        {
                            row.remove("age");
                        }
                        else
                        {
                            long age = Long.parseLong(ageValue);
                            row.put("age", age);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("invalid age value");
                    }
                }
                case "cost" ->
                {
                    try
                    {
                        String costValue = cellValue.split("=")[1].replaceAll("\\s", "");

                        if (costValue.equals("null"))
                        {
                            row.remove("cost");
                        }
                        else
                        {
                            double cost = Double.parseDouble(costValue);
                            row.put("cost", cost);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("invalid cost value");
                    }
                }
                case "active" ->
                {
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
                        throw new IllegalArgumentException("invalid active value");
                    }
                }
                default -> throw new IllegalArgumentException("invalid column name");
            }
        }
    }

    private List<Map<String, Object>> selectRowsWhere(String request) throws ArrayIndexOutOfBoundsException
    {
        String whereSection = request.split("(?i)WHERE")[1];

        if (whereSection.contains("null"))
        {
            throw new IllegalArgumentException("null values cannot be passed for comparison");
        }

        String[] orSections = whereSection.split("(?i)OR");
        List<Map<String, Object>> rowsWhereForOr = new ArrayList<>();

        for (String orSection : orSections)
        {
            String[] andSections = orSection.split("(?i)AND");
            List<Map<String, Object>> rowsWhereForAnd = cloneTable(table);

            for (String andSection : andSections)
            {
                switch (andSection.split("'")[1].toLowerCase())
                {
                    case "id" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);
                        long id;

                        try
                        {
                            id = Long.parseLong(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                        }
                        catch (Exception e)
                        {
                            throw new IllegalArgumentException("invalid id value");
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
                                    if (!checkConditionForNumbers((long) pair.getValue(), operatorOfCondition, id))
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
                        if (andSection.replaceAll("\\s", "").endsWith("'"))
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
                                        if (!checkConditionForString((String) pair.getValue(), operatorOfCondition, lastName))
                                        {
                                            rowIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            throw new IllegalArgumentException("invalid lastName value");
                        }
                    }
                    case "age" ->
                    {
                        String operatorOfCondition = parseOperatorOfCondition(andSection);
                        long age;

                        try
                        {
                            age = Long.parseLong(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                        }
                        catch (Exception e)
                        {
                            throw new IllegalArgumentException("invalid age value");
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
                                    if (!checkConditionForNumbers((long) pair.getValue(), operatorOfCondition, age))
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

                        double cost;

                        try
                        {
                            cost = Double.parseDouble(andSection.split(operatorOfCondition)[1].replaceAll("\\s", ""));
                        }
                        catch (Exception e)
                        {
                            throw new IllegalArgumentException("invalid cost value");
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
                                    if (!checkConditionForNumbers((double) pair.getValue(), operatorOfCondition, cost))
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
                            throw new IllegalArgumentException("find id: invalid active value");
                        }
                    }
                    default -> throw new IllegalArgumentException("there is no such column");
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

    private <T extends Number & Comparable<T>> boolean checkConditionForNumbers(T obj1, String condition, T obj2)
    {
        switch (condition)
        {
            case ">=" -> { return obj1.compareTo(obj2) >= 0; }
            case "<=" -> { return obj1.compareTo(obj2) <= 0; }
            case "!=" -> { return obj1.compareTo(obj2) != 0; }
            case "=" -> { return obj1.compareTo(obj2) == 0; }
            case ">" -> { return obj1.compareTo(obj2) > 0; }
            case "<" -> { return obj1.compareTo(obj2) < 0; }
            default -> throw new IllegalArgumentException("no such condition");
        }
    }

    private boolean checkConditionForBoolean(boolean obj1, String condition, boolean obj2)
    {
        switch (condition)
        {
            case "=" -> { return obj1 == obj2; }
            case "!=" -> { return obj1 != obj2; }
            default -> throw new IllegalArgumentException("no such condition");
        }
    }

    private boolean checkConditionForString(String obj1, String condition, String obj2)
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
            default -> throw new IllegalArgumentException("no such condition");
        }
    }

    private String parseOperatorOfCondition(String condition)
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
            throw new IllegalArgumentException("no such operator in condition");
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

    private List<Map<String, Object>> delete(String request)
    {
        if (request.trim().equalsIgnoreCase("DELETE"))
        {
            List<Map<String, Object>> deleteTable = cloneTable(table);
            table.clear();
            return deleteTable;
        }

        if (!(request.split("\\s+")[1].equalsIgnoreCase("WHERE")
                && request.replaceAll("\\s+", "").toUpperCase().startsWith("DELETEWHERE'")))
        {
            throw new IllegalArgumentException("there is no such command");
        }

        List<Map<String, Object>> rowsWhere = selectRowsWhere(request);

        for(Map<String, Object> rowWhere : rowsWhere)
        {
            table.removeIf(row -> row.equals(rowWhere));
        }

        return rowsWhere;
    }

    private List<Map<String, Object>> select(String request)
    {
        if (request.trim().equalsIgnoreCase("SELECT"))
        {
            return cloneTable(table);
        }

        if (!request.split("\\s+")[1].equalsIgnoreCase("WHERE"))
        {
            throw new IllegalArgumentException("there is no such command");
        }

        return selectRowsWhere(request);
    }
}
