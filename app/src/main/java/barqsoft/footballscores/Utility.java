package barqsoft.footballscores;

import android.content.Context;

import java.io.IOException;

import barqsoft.footballscores.service.FetchService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility {
    private static final String LOG_TAG = "Utility";
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

    public static int getTeamCrestByTeamName(String teamName) {
        switch (teamName) {
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            case "Crystal Palace FC":
                return R.drawable.crystal_palace_fc;
            case "1. FC Union Berlin":
                return R.drawable.one_fc_union_berlin;
            case "Genoa CFC":
                return R.drawable.genoa_cfc;
            case "US Sassuolo Calcio":
                return R.drawable.us_sassuolo_calcio;
            case "West Bromwich Albion FC":
                return R.drawable.us_sassuolo_calcio;
            case "Frosinone Calcio":
                return R.drawable.frosinonestemma;
            case "AC Chievo Verona":
                return R.drawable.ac_chievo_verona;
            case "SSC Napoli":
                return R.drawable.ssc_napoli_logo;
            case "Liverpool FC":
                return R.drawable.fc_liverpool;
            case "Getafe CF":
                return R.drawable.getafe_cf;
            case "Bologna FC":
                return R.drawable.fc_bologna;
            case "Werder Bremen":
                return R.drawable.sv_werder_bremen_logo;
            case "Swansea City FC":
                return R.drawable.swansea_city_afc;
            case "1. FC Kaiserslautern":
                return R.drawable.logo_on1_fc_kaiserslautern;
            case "SV Sandhausen":
                return R.drawable.sv_sandhausen;
            case "Torino FC":
                return R.drawable.torino_fc_logo;
            case "AS Roma":
                return R.drawable.as_rom;
            default:
                return R.drawable.no_icon;
        }
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
