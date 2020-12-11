package edu.uwlax.himal.api;

import edu.uwlax.himal.Himalayan;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class PeakAnalysis
{
    @Autowired
    private Himalayan himalayan;

    @Autowired
    private PublicResources resources;

    private float parseFloat(String string)
    {
        try
        {
            return Float.parseFloat(string);
        } catch (NumberFormatException ex)
        {
            return 0.0f;
        }
    }

    private int parseInt(String string)
    {
        try
        {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    @GetMapping("/peak-analysis")
    @ResponseBody
    public String mappedAggregates()
    {
        JSONArray array = new JSONArray();

        HashMap<String, HashMap<Integer, Integer>> expedPerYear = new HashMap<>();

        HashMap<String, Integer> accumExped = new HashMap<>();
        HashMap<String, Integer> accumSuccess = new HashMap<>();

        for (JSONObject expedition : himalayan.expeditions.getContents())
        {
            if (expedition.has("PEAKID"))
            {
                String peakID = expedition.getString("PEAKID");
                accumExped.put(peakID, accumExped.getOrDefault(peakID, 0) + 1);

                HashMap<Integer, Integer> perYear = expedPerYear.computeIfAbsent(peakID, (String key) -> new HashMap<>());

                if (expedition.has("YEAR"))
                {
                    try
                    {
                        int year = Integer.parseInt(expedition.getString("YEAR"));
                        perYear.put(year, perYear.getOrDefault(year, 0) + 1);
                    } catch (NumberFormatException ex)
                    { }
                }

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

            result.put("peak-name", object.get("PKNAME"));
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

            HashMap<Integer, Integer> perYear = expedPerYear.computeIfAbsent(peakID, (String key) -> new HashMap<>());

            JSONArray years = new JSONArray();
            List<Integer> keysAsList = new ArrayList<>(perYear.keySet());

            keysAsList.sort(Comparator.comparingInt(a -> a));

            for (int year : keysAsList)
            {
                JSONObject y = new JSONObject();

                y.put("year", year);
                y.put("expedition-count", perYear.get(year));

                years.put(y);
            }

            result.put("expeditions-per-year", years);

            array.put(result);
        }

        return array.toString();
    }

    @GetMapping("/peak-analysis/{peak-id}")
    @ResponseBody
    public String mappedAggregates(
            @PathVariable("peak-id")
            String peakID
    )
    {
        JSONArray result = new JSONArray();
        JSONArray exped = resources.getExpeditions(String.format("PEAKID equals %s", peakID));

        for (Object obj : exped)
        {
            JSONObject e = (JSONObject)obj;
            JSONObject r = new JSONObject();

            if (e.has("APPROACH"))
                r.put("approach-march", e.get("APPROACH"));
            else
                r.put("approach-march", "Not listed");

            if (e.has("EXPID"))
                r.put("expedition-id", e.get("EXPID"));
            else
                r.put("expedition-id", "Not listed");

            if (e.has("PEAKID"))
                r.put("peak-id", e.get("PEAKID"));
            else
                r.put("peak-id", "Not listed");

            if (e.has("YEAR"))
                r.put("year", parseInt(e.getString("YEAR")));
            else
                r.put("year", 0);

            if (e.has("NATION"))
                r.put("principle-nationality", e.get("NATION"));
            else
                r.put("principle-nationality", "Not listed");

            if (e.has("LEADERS"))
                r.put("leaders", e.get("LEADERS"));
            else
                r.put("leaders", "Not listed");

            if (e.has("SPONSOR"))
                r.put("sponsor", e.get("SPONSOR"));
            else
                r.put("sponsor", "Not listed");

            if (e.has("CLAIMED"))
                r.put("success-claimed", e.get("CLAIMED").equals("T"));
            else
                r.put("success-claimed", false);

            if (e.has("DISPUTED"))
                r.put("success-disputed", e.get("DISPUTED").equals("T"));
            else
                r.put("success-disputed", false);

            if (e.has("O2USED"))
                r.put("oxygen-used", e.get("O2USED").equals("T"));
            else
                r.put("oxygen-used", false);

            if (e.has("O2TAKEN"))
                r.put("oxygen-taken", e.get("O2TAKEN").equals("T"));
            else
                r.put("oxygen-taken", false);

            if (e.has("COUNTRIES"))
                r.put("other-countries", e.get("COUNTRIES"));
            else
                r.put("other-countries", "Not listed");

            if (e.has("SMTDATE"))
                r.put("summit-date", e.get("SMTDATE"));
            else
                r.put("summit-date", "Not listed");

            if (e.has("SMTTIME"))
                r.put("summit-time", e.get("SMTTIME"));
            else
                r.put("summit-time", "Not listed");

            if (e.has("SMTDAYS"))
                r.put("summit-days", parseInt(e.getString("SMTDAYS")));
            else
                r.put("summit-days", 0);

            if (e.has("TOTDAYS"))
                r.put("total-days", parseInt(e.getString("TOTDAYS")));
            else
                r.put("total-days", 0);

            if (e.has("TERMDATE"))
                r.put("termination-date", e.get("TERMDATE"));
            else
                r.put("termination-date", "Not listed");

            if (e.has("TERMNOTE"))
                r.put("termination-note", e.get("TERMNOTE"));
            else
                r.put("termination-note", "Not listed");

            if (e.has("HIGHPOINT"))
                r.put("high-point", parseFloat(e.getString("HIGHPOINT")));
            else
                r.put("high-point", 0.0f);

            if (e.has("CAMPSITES"))
                r.put("campsites", e.get("CAMPSITES"));
            else
                r.put("campsites", "Not listed");

            if (e.has("ACCIDENTS"))
                r.put("accidents", e.get("ACCIDENTS"));
            else
                r.put("accidents", "Not listed");

            if (e.has("ACHIEVMENT"))
                r.put("achievements", e.get("ACHIEVMENT"));
            else
                r.put("achievements", "Not listed");

            if (e.has("AGENCY"))
                r.put("trekking-agency", e.get("AGENCY"));
            else
                r.put("trekking-agency", "Not listed");

            if (e.has("ROPE"))
                r.put("rope-length", parseFloat(e.getString("ROPE")));
            else
                r.put("rope-length", 0.0f);

            if (e.has("TOTMEMBERS"))
                r.put("total-members", parseInt(e.getString("TOTMEMBERS")));
            else
                r.put("total-members", 0);

            if (e.has("MDEATHS"))
                r.put("member-deaths", parseInt(e.getString("MDEATHS")));
            else
                r.put("member-deaths", 0);

            if (e.has("TOTHIRED"))
                r.put("total-hired",parseInt(e.getString("TOTHIRED")));
            else
                r.put("total-hired", 0);

            if (e.has("HDEATHS"))
                r.put("hired-deaths", parseInt(e.getString("HDEATHS")));
            else
                r.put("hired-deaths", 0);


            if (e.has("HOST"))
                switch(e.getString("HOST"))
                {
                    case "0":
                        r.put("host-country", "Unknown");
                        break;
                    case "1":
                        r.put("host-country", "Nepal");
                        break;
                    case "2":
                        r.put("host-country", "China");
                        break;
                    case "3":
                        r.put("host-country", "India");
                        break;
                }
            else
                r.put("host-country", "Not listed");

            if (e.has("SEASON"))
                switch(e.getString("SEASON"))
                {
                    case "0":
                        r.put("season", "Unknown");
                        break;
                    case "1":
                        r.put("season", "Spring");
                        break;
                    case "2":
                        r.put("season", "Summer");
                        break;
                    case "3":
                        r.put("season", "Autumn");
                        break;
                    case "4":
                        r.put("season", "Winter");
                        break;
                }
            else
                r.put("season", "Not listed");

            if (e.has("TERMREASON"))
                switch (e.getString("TERMREASON"))
                {
                    case "0":
                        r.put("termination-reason", "Unknown");
                        break;
                    case "1":
                        r.put("termination-reason", "Success (main peak)");
                        break;
                    case "2":
                        r.put("termination-reason", "Success (sub-peak, foresummit)");
                        break;
                    case "3":
                        r.put("termination-reason", "Success (claimed)");
                        break;
                    case "4":
                        r.put("termination-reason", "Bad weather (storms, high winds)");
                        break;
                    case "5":
                        r.put("termination-reason", "Bad conditions (deep snow, avalanches, falling ice, or rock)");
                        break;
                    case "6":
                        r.put("termination-reason", "Accident (death or serious injury)");
                        break;
                    case "7":
                        r.put("termination-reason", "Illness, AMS, exhaustion, or frostbite");
                        break;
                    case "8":
                        r.put("termination-reason", "Lack of (or loss) of supplies, support, or equipment");
                        break;
                    case "9":
                        r.put("termination-reason", "Lack of time");
                        break;
                    case "10":
                        r.put("termination-reason", "Route technically too difficult, lack of experience, strength, or motivation");
                        break;
                    case "11":
                        r.put("termination-reason", "Did not reach basecamp");
                        break;
                    case "12":
                        r.put("termination-reason", "Did not attempt climb");
                        break;
                    case "13":
                        r.put("termination-reason", "Attempt rumored");
                        break;
                    case "14":
                        r.put("termination-reason", "Other");
                        break;
                }
            else
                r.put("termination-reason", "Not listed");

            int numRoutes = 4;
            JSONArray routes = new JSONArray();

            for (int i = 1; i <= numRoutes; i++)
            {
                JSONObject route = new JSONObject();

                if (e.has("ROUTE" + i))
                {
                    String ro = e.getString("ROUTE" + i);
                    if (ro.isBlank())
                        continue;

                    route.put("route", ro);
                }

                if (e.has("SUCCESS" + i))
                    route.put("success", e.get("SUCCESS" + i).equals("T"));
//                if (e.has("ASCENT" + i))
//                    route.put("ascent-number", e.get("ASCENT" + i));

                routes.put(route);
            }

            r.put("routes", routes);

            result.put(r);
        }

        return result.toString();//4)
//                .replace(" ", "&nbsp;")
//                .replace("\n", "<br />");
    }
}
