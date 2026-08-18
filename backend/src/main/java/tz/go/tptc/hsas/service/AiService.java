package tz.go.tptc.hsas.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class AiService {
    private final AnalyticsService analytics;

    public AiService(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> analysis(Integer borderPostId) {
        Map<String, Object> cop = analytics.cop(borderPostId);
        Map<String, Object> kpis = analytics.kpis(borderPostId);
        Map<String, Object> trends = analytics.trends(30, borderPostId);
        List<Map<String, Object>> heatmap = analytics.heatmap();
        List<Map<String, Object>> daily = (List<Map<String, Object>>) trends.get("daily");
        List<Map<String, Object>> anomalies = detectAnomalies(heatmap, kpis, cop);
        Map<String, Object> predictions = predict(daily);
        List<Map<String, Object>> riskScores = heatmap.stream()
                .sorted((a, b) -> Integer.compare((Integer) b.get("risk_score"), (Integer) a.get("risk_score")))
                .toList();
        String summary = executiveSummary(cop, kpis, anomalies, predictions, riskScores);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("summary", summary);
        out.put("anomalies", anomalies);
        out.put("predictions", predictions);
        out.put("risk_scores", riskScores);
        out.put("recommendations", recommendations(cop, anomalies, predictions));
        out.put("confidence", daily.size() >= 14 ? 78 : 55);
        out.put("generated_at", Instant.now().toString());
        out.put("border_post_id", borderPostId);
        return out;
    }

    public Map<String, Object> ask(String question, Integer borderPostId) {
        Map<String, Object> cop = analytics.cop(borderPostId);
        Map<String, Object> kpis = analytics.kpis(borderPostId);
        String q = question == null ? "" : question.toLowerCase();
        String answer;
        if (q.contains("threat") || q.contains("cop") || q.contains("risk")) {
            answer = "National threat level is " + cop.get("threat_label") + ". Open intelligence: "
                    + cop.get("open_intelligence") + ", critical: " + cop.get("critical_intelligence") + ".";
        } else if (q.contains("entry") || q.contains("movement") || q.contains("migrat")) {
            answer = "Today there are " + kpis.get("entries_today") + " entries and " + kpis.get("exits_today")
                    + " exits. Refugee/asylum entries (30d): " + cop.get("refugee_entries_30d") + ".";
        } else if (q.contains("health") || q.contains("disease") || q.contains("cholera")) {
            answer = "Active disease outbreak alerts: " + cop.get("disease_outbreak_alerts") + ".";
        } else if (q.contains("alert") || q.contains("incident")) {
            answer = "Open alerts: " + kpis.get("open_alerts") + ". Open incidents: " + kpis.get("open_incidents") + ".";
        } else if (q.contains("camp") || q.contains("refugee")) {
            answer = "Camp population is " + cop.get("refugee_population") + " across western Tanzania camps.";
        } else {
            answer = "HSAS AI (rule-based): " + cop.get("threat_label") + " threat. "
                    + kpis.get("entries_today") + " entries today, " + kpis.get("open_alerts")
                    + " open alerts. Ask about threat, movements, health, alerts or camps.";
        }
        return Map.of("question", question, "answer", answer, "generated_at", Instant.now().toString());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> detectAnomalies(List<Map<String, Object>> heatmap, Map<String, Object> kpis, Map<String, Object> cop) {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        for (Map<String, Object> post : heatmap) {
            if ("high".equals(post.get("level"))) {
                anomalies.add(Map.of(
                        "type", "security_escalation",
                        "severity", "high",
                        "title", "Elevated border risk",
                        "message", post.get("name") + " risk score is " + post.get("risk_score") + "/100.",
                        "border", post.get("name"),
                        "score", post.get("risk_score")));
            }
        }
        long disease = ((Number) cop.get("disease_outbreak_alerts")).longValue();
        if (disease > 0) {
            anomalies.add(Map.of("type", "health", "severity", "high", "title", "Disease outbreak signal",
                    "message", disease + " active health outbreak report(s).", "score", 80));
        }
        long flagged = ((Number) kpis.get("flagged_count")).longValue();
        if (flagged >= 3) {
            anomalies.add(Map.of("type", "identity", "severity", "medium", "title", "Flagged movements",
                    "message", flagged + " flagged movement records require review.", "score", 60));
        }
        return anomalies;
    }

    private Map<String, Object> predict(List<Map<String, Object>> daily) {
        int n = daily.size();
        long last7 = daily.subList(Math.max(0, n - 7), n).stream().mapToLong(d -> ((Number) d.get("entries")).longValue()).sum();
        long prev7 = daily.subList(Math.max(0, n - 14), Math.max(0, n - 7)).stream().mapToLong(d -> ((Number) d.get("entries")).longValue()).sum();
        String direction = last7 > prev7 * 1.1 ? "rising" : (last7 < prev7 * 0.9 ? "falling" : "stable");
        long change = prev7 == 0 ? 0 : Math.round(((last7 - prev7) * 100.0) / prev7);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("next_7_days", last7);
        m.put("trend_direction", direction);
        m.put("change_pct", change);
        return m;
    }

    private String executiveSummary(Map<String, Object> cop, Map<String, Object> kpis,
                                    List<Map<String, Object>> anomalies, Map<String, Object> predictions,
                                    List<Map<String, Object>> riskScores) {
        String highest = riskScores.isEmpty() ? "All posts within normal range."
                : "Highest-risk border: " + riskScores.get(0).get("name") + " (" + riskScores.get(0).get("risk_score") + "/100).";
        String anomaly = anomalies.isEmpty() ? "No significant anomalies detected."
                : "AI detected " + anomalies.size() + " anomal" + (anomalies.size() == 1 ? "y" : "ies") + " requiring attention.";
        return "National threat level is " + cop.get("threat_label") + ". Today: " + kpis.get("entries_today")
                + " entries, " + kpis.get("open_alerts") + " open alerts, " + cop.get("refugee_entries_30d")
                + " refugee/asylum entries (30d), " + cop.get("disease_outbreak_alerts")
                + " active disease alerts. " + highest + " " + anomaly + " Migration trend is "
                + predictions.get("trend_direction") + ".";
    }

    private List<String> recommendations(Map<String, Object> cop, List<Map<String, Object>> anomalies, Map<String, Object> predictions) {
        List<String> recs = new ArrayList<>();
        if ("red".equals(cop.get("threat_level")) || "orange".equals(cop.get("threat_level"))) {
            recs.add("Activate HQ COP watch and increase night patrols at western posts.");
        }
        if (((Number) cop.get("disease_outbreak_alerts")).longValue() > 0) {
            recs.add("Deploy health screening and water purification at affected transit centres.");
        }
        if ("rising".equals(predictions.get("trend_direction"))) {
            recs.add("Pre-position reception capacity at Kigoma and Bukoba for expected inflow.");
        }
        if (anomalies.isEmpty()) {
            recs.add("Maintain routine monitoring across the 16 data-matrix modules.");
        }
        recs.add("Review recommended actions from open intelligence reports.");
        return recs;
    }
}
