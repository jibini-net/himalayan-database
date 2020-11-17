package edu.uwlax.himal.api;

import edu.uwlax.himal.Himalayan;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class PeakAnalysis
{
    @Autowired
    private Himalayan himalayan;

    @Autowired
    private PublicResources resources;

    @GetMapping("/peak-analysis")
    @ResponseBody
    public String mappedAggregates()
    {
        JSONArray array = new JSONArray();

        HashMap<String, Integer> accumExped = new HashMap<>();
        HashMap<String, Integer> accumSuccess = new HashMap<>();

        for (JSONObject expedition : himalayan.expeditions.getContents())
        {
            if (expedition.has("PEAKID"))
            {
                String peakID = expedition.getString("PEAKID");
                accumExped.put(peakID, accumExped.getOrDefault(peakID, 0) + 1);

                if (expedition.has("TERMREASON"))
                    switch (expedition.getString("TERMREASON"))
                    {
                        // Success (main peak)
                        case "1":
                        // Success (subpeak, foresummit)
                        case "2":
                        // Success (claimed)
                        case "3":
                            accumSuccess.put(peakID, accumSuccess.getOrDefault(peakID, 0) + 1);
                            break;
                    }
            }
        }

        for (JSONObject object : himalayan.peaks.getContents())
        {
            JSONObject result = new JSONObject();

            String peakID = object.getString("PEAKID");

            result.put("peak-id", peakID);
            result.put("peak-name", peakID);

            result.put("peak-climbed", object.get("PSTATUS").equals("2"));

            try
            {
                result.put("peak-height", Float.parseFloat(object.getString("HEIGHTM")));
            } catch (NumberFormatException ex)
            {
                result.put("peak-height", 0);
            }

            int numExped = accumExped.getOrDefault(peakID, 0);
            int numSuccess = accumSuccess.getOrDefault(peakID, 0);

            result.put("num-expeditions", numExped);
            result.put("num-successful", numSuccess);

            array.put(result);
        }

        return array.toString(4);
    }
}
