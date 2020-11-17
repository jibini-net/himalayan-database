package edu.uwlax.himal.api;

import edu.uwlax.himal.Himalayan;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        for (JSONObject object : himalayan.peaks.getContents())
        {
            JSONObject result = new JSONObject();

            result.put("peak-id", object.get("PEAKID"));
            result.put("peak-name", object.get("PKNAME"));

            result.put("peak-climbed", object.get("PSTATUS").equals("2"));

            try
            {
                result.put("peak-height", Float.parseFloat(object.getString("HEIGHTM")));
            } catch (NumberFormatException ex)
            {
                result.put("peak-height", 0);
            }

            JSONArray relatedExpeditions = resources.getExpeditions(
                    String.format("PEAKID equals %s", object.get("PEAKID"))
            );

            int success = 0;

            for (Object expedition : relatedExpeditions)
            {
                JSONObject exp = (JSONObject)expedition;

                if (exp.has("TERMREASON"))
                    switch (exp.getString("TERMREASON"))
                    {
                        // Success (main peak)
                        case "1":
                        // Success (subpeak, foresummit)
                        case "2":
                        // Success (claimed)
                        case "3":
                            success++;

                            break;
                    }
            }

            result.put("num-expeditions", relatedExpeditions.length());
            result.put("num-successful", success);

            array.put(result);
        }

        return array.toString(4);
    }
}
