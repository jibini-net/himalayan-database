package edu.uwlax.himal.api;

import edu.uwlax.himal.Himalayan;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Scanner;

@Controller
public class PublicResources
{
    @Autowired
    private Himalayan himalayan;

    private boolean passes(String filter, JSONObject values)
    {
        Scanner scan = new Scanner(filter);

        while (scan.hasNext())
        {
            String field = scan.next();
            String operator = scan.next();
            String arg = scan.next();

            if (!values.has(field))
                throw new IllegalStateException(String.format("Invalid filter field name '%s'", field));
            String fieldValue = values.getString(field);

            switch (operator)
            {
                case "equals":
                    if (!fieldValue.equals(arg))
                        return false;
                    break;

                case "contains":
                    if (!fieldValue.toLowerCase().contains(arg.toLowerCase()))
                        return false;
                    break;

                default:
                    throw new IllegalStateException(String.format("Invalid filter operator '%s'", operator));
            }
        }

        return true;
    }

    @GetMapping("/member")
    @ResponseBody
    public String mappedGetMembers(
            @RequestParam(defaultValue = "") String filter
    )
    {
        JSONArray array = new JSONArray();

        for (JSONObject object : himalayan.members.getContents())
        {
            if (passes(filter, object))
                array.put(object);
        }

        return array.toString();
    }

    @GetMapping("/peak")
    @ResponseBody
    public String mappedGetPeaks(
            @RequestParam(defaultValue = "") String filter
    )
    {
        JSONArray array = new JSONArray();

        for (JSONObject object : himalayan.peaks.getContents())
        {
            String id = object.getString("PEAKID");
            JSONObject knownCoords = himalayan.auxPeaks.get("PEAKID", id);

            if (knownCoords == null)
                object.put("COORDSKNOWN", "F");
            else
            {
                object.put("COORDSKNOWN", "T");

                for (String key : knownCoords.keySet())
                    object.put(key, knownCoords.get(key));
            }

            if (passes(filter, object))
                array.put(object);
        }

        return array.toString();
    }

    @GetMapping("/expedition")
    @ResponseBody
    public String mappedGetExpeditions(
            @RequestParam(defaultValue = "") String filter
    )
    {
        JSONArray array = new JSONArray();

        for (JSONObject object : himalayan.expeditions.getContents())
        {
            if (passes(filter, object))
                array.put(object);
        }

        return array.toString();
    }
}
