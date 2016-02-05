package barqsoft.footballscores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import barqsoft.footballscores.data.DatabaseContract.Crest;
import barqsoft.footballscores.service.FetchService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static barqsoft.footballscores.data.DatabaseContract.Crest.COL_TEAM_NAME;
import static barqsoft.footballscores.sync.SyncAdapter.CREST_PROJECTION;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility {
    public static final int CHAMPIONS_LEAGUE = 362;
    static OkHttpClient sClient = new OkHttpClient();
    private static final String CREST_URL = "crestUrl";


    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case FetchService.SERIE_A:
                return context.getString(R.string.seriaa);
            case FetchService.PREMIER_LEAGUE:
                return context.getString(R.string.premierleague);
            case FetchService.PRIMERA_DIVISION:
                return context.getString(R.string.primeradivison);
            case FetchService.BUNDESLIGA2:
                return context.getString(R.string.bundesliga2);
            case FetchService.BUNDESLIGA1:
                return context.getString(R.string.bundesliga1);
            default:
                return context.getString(R.string.not_known_league);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.match_day_6);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.semi_final);
            } else {
                return context.getString(R.string.final_text);
            }
        } else {
            return context.getString(R.string.matchday_text, String.valueOf(match_day));
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static String getTeamCrestByTeamName(
            Context context, String teamUrl, String teamName) {
//        if (teamName == null) {
//            return R.drawable.no_icon;
//        }
        switch (teamName) {
            case "Manchester United FC":
                return context.getString(R.string.Manchester_United_FC);
            case "Swansea City":
                return context.getString(R.string.Swansea_City_FC);
            case "Leicester City":
                return context.getString(R.string.Leicester_City_FC);
            case "Everton FC":
                return context.getString(R.string.Everton_FC);
            case "West Ham United FC":
                return context.getString(R.string.West_Ham_United_FC);
            case "Tottenham Hotspur FC":
                return context.getString(R.string.Tottenham_Hotspur_FC);
            case "West Bromwich Albion":
                return context.getString(R.string.West_Bromwich_Albion_FC);
            case "Sunderland AFC":
                return context.getString(R.string.Sunderland_AFC);
            case "Crystal Palace FC":
                return context.getString(R.string.Crystal_Palace_FC);
            case "Stoke City FC":
                return context.getString(R.string.Stoke_City_FC);
            case "1. FC Union Berlin":
                return context.getString(R.string.one_FC_Union_Berlin);
            case "Genoa CFC":
                return context.getString(R.string.Genoa_CFC);
            case "US Sassuolo Calcio":
                return context.getString(R.string.US_Sassuolo_Calcio);
            case "West Bromwich Albion FC":
                return context.getString(R.string.West_Bromwich_Albion_FC);
            case "Frosinone Calcio":
                return context.getString(R.string.Frosinone_Calcio);
            case "AC Chievo Verona":
                return context.getString(R.string.AC_Chievo_Verona);
            case "SSC Napoli":
                return context.getString(R.string.SSC_Napoli);
            case "Liverpool FC":
                return context.getString(R.string.Liverpool_FC);
            case "Getafe CF":
                return context.getString(R.string.Getafe_CF);
            case "Bologna FC":
                return context.getString(R.string.Bologna_FC);
            case "Werder Bremen":
                return context.getString(R.string.Werder_Bremen);
            case "UC Sampdoria":
                return context.getString(R.string.UC_Sampdoria);
            case "Swansea City FC":
                return context.getString(R.string.Swansea_City_FC);
            case "1. FC Kaiserslautern":
                return context.getString(R.string.one_FC_Kaiserslautern);
            case "Málaga CF":
                return context.getString(R.string.Málaga_CF);
            case "SV Sandhausen":
                return context.getString(R.string.SV_Sandhausen);
            case "FC Internazionale Milano":
                return context.getString(R.string.FC_Internazionale_Milano);
            case "Torino FC":
                return context.getString(R.string.Torino_FC);
            case "AS Roma":
                return context.getString(R.string.AS_Roma);
            case "Bor. Mönchengladbach":
                return context.getString(R.string.Bor_Mönchengladbach);
            case "Carpi FC":
                return context.getString(R.string.Carpi_FC);
            case "VfL Bochum":
                return context.getString(R.string.VfL_Bochum);
            case "SS Lazio":
                return context.getString(R.string.SS_Lazio);
            case "SC Paderborn 07":
                return context.getString(R.string.SC_Paderborn_07);
            case "Empoli FC":
                return context.getString(R.string.Empoli_FC);
            case "Manchester City FC":
                return context.getString(R.string.Manchester_City_FC);
            case "Watford FC":
                return context.getString(R.string.Watford_FC);
            case "Leicester City FC":
                return context.getString(R.string.Leicester_City_FC);
            case "Juventus Turin":
                return context.getString(R.string.Juventus_Turin);
            case "US Cittá di Palermo":
                return context.getString(R.string.US_Cittá_di_Palermo);
            case "Southampton FC":
                return context.getString(R.string.Southampton_FC);
            case "Hellas Verona FC":
                return context.getString(R.string.Hellas_Verona_FC);
            case "Chelsea FC":
                return context.getString(R.string.Chelsea_FC);
            case "Atalanta BC":
                return context.getString(R.string.Atalanta_BC);
            case "Arsenal FC":
                return context.getString(R.string.Arsenal_FC);
            case "Udinese Calcio":
                return context.getString(R.string.Udinese_Calcio);
            case "Aston Villa FC":
                return context.getString(R.string.Aston_Villa_FC);
            case "ACF Fiorentina":
                return context.getString(R.string.ACF_Fiorentina);
            case "AFC Bournemouth":
                return context.getString(R.string.AFC_Bournemouth);
            case "SC Freiburg":
                return context.getString(R.string.SC_Freiburg);
        }

        String res = "";
        Cursor c = context.getContentResolver().query(
                Crest.CONTENT_URI, CREST_PROJECTION, COL_TEAM_NAME + "=?", new String[]{teamName}, null);
        if (c != null && c.moveToFirst()) {
            if (c.getCount() != 0) {
                res = c.getString(1);
            }
            c.close();
            return res;
        }

        return res;
    }

    public static String getCrestUrl(String teamUrl) {
        String crest = "";

        String response = null;
        try {
            response = Utility.sendGetRequestAndGetResponse(teamUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            crest = new JSONObject(response).getString(CREST_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return crest;
    }

    public static String sendGetRequestAndGetResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", BuildConfig.FOOTBALL_DATA_API_KEY)
                .build();

        Response response = sClient.newCall(request).execute();

        return response.body().string();
    }
}
