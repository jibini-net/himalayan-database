package edu.uwlax.himal.api;

import edu.uwlax.himal.Himalayan;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PublicResources
{
    @Autowired
    private Himalayan himalayan;

    @GetMapping("/member")
    @ResponseBody
    public String mappedGetMembers()
    {
        JSONArray array = new JSONArray();
        for (JSONObject object : himalayan.members.getContents())
            array.put(object);

        return array.toString();
    }

    @GetMapping("/peak")
    @ResponseBody
    public String mappedGetPeaks()
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

            array.put(object);
        }

        return array.toString();
    }

    @GetMapping("/expedition")
    @ResponseBody
    public String mappedGetExpeditions()
    {
        JSONArray array = new JSONArray();
        for (JSONObject object : himalayan.expeditions.getContents())
            array.put(object);

        return array.toString();
    }
}
