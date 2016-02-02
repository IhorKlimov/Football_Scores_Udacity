package barqsoft.footballscores;

import android.content.Context;

import barqsoft.footballscores.service.FetchService;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility {
    public static final int CHAMPIONS_LEAGUE = 362;

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
            return context.getString(R.string.matchday_text,String.valueOf(match_day)) ;
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
        if (teamName == null) {
            return R.drawable.no_icon;
        }
        switch (teamName) {
            //This is the set of icons that are currently in the app.
            // Feel free to find and add more as you go.
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
            default:
                return R.drawable.no_icon;
        }
    }
}
