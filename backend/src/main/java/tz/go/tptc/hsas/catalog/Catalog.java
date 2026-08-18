package tz.go.tptc.hsas.catalog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Catalog {
    private Catalog() {}

    public static Map<String, Object> domains() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("security", Map.of("label", "Security & Border Intelligence", "icon", "shield-exclamation", "color", "#dc2626"));
        map.put("health", Map.of("label", "Public Health Intelligence", "icon", "heart-pulse", "color", "#059669"));
        map.put("humanitarian", Map.of("label", "Humanitarian Information", "icon", "people", "color", "#2563eb"));
        map.put("economic", Map.of("label", "Economic Intelligence", "icon", "currency-exchange", "color", "#d97706"));
        map.put("environmental", Map.of("label", "Environmental Information", "icon", "cloud-rain", "color", "#0891b2"));
        map.put("infrastructure", Map.of("label", "Infrastructure Status", "icon", "building", "color", "#64748b"));
        map.put("logistics", Map.of("label", "Logistics Information", "icon", "box-seam", "color", "#7c3aed"));
        map.put("population", Map.of("label", "Population & Demographics", "icon", "bar-chart", "color", "#0d3b66"));
        return map;
    }

    public static Map<String, Map<String, String>> categories() {
        return Map.of(
                "security", map(
                        "refugee_entries", "Refugee Border Entries",
                        "illegal_crossing", "Illegal Border Crossing",
                        "armed_group", "Armed Group Activities",
                        "terrorism_threat", "Terrorism / Extreme Movements",
                        "weapons_smuggling", "Weapons Smuggling",
                        "drugs_smuggling", "Drugs Smuggling",
                        "wildlife_smuggling", "Wildlife Smuggling",
                        "mineral_smuggling", "Minerals Smuggling",
                        "human_trafficking", "Human Trafficking",
                        "border_conflict", "Border Conflicts",
                        "cattle_theft", "Cross-Border Cattle Theft",
                        "border_riot", "Demonstrations / Riots Near Border",
                        "crime_statistics", "Crime Statistics"),
                "health", map(
                        "disease_outbreak", "Disease Outbreak",
                        "infected_patients", "Infected Patients Count",
                        "vaccination_coverage", "Vaccination Coverage",
                        "refugee_health", "Refugee Health Status",
                        "water_quality", "Water Quality Report",
                        "food_safety", "Food Safety Issues"),
                "humanitarian", map(
                        "refugee_population", "Refugee Population by Camp",
                        "new_arrivals", "New Arrivals",
                        "food_availability", "Food Availability",
                        "shelter_conditions", "Shelter Conditions",
                        "water_supply", "Water Supply",
                        "ngo_activities", "NGO Activities"),
                "economic", map(
                        "commodity_prices", "Market Prices (Essential Goods)",
                        "food_shortage", "Food Shortages",
                        "fuel_availability", "Fuel Availability",
                        "inflation", "Inflation Indicators",
                        "livestock_disease", "Livestock Diseases",
                        "agricultural_production", "Agricultural Production",
                        "trade_volume", "Cross-Border Trade Volume",
                        "imports_exports", "Major Imports / Exports",
                        "employment", "Employment Situation"),
                "environmental", map(
                        "floods", "Floods",
                        "drought", "Drought",
                        "wildfires", "Wildfires",
                        "landslides", "Landslides",
                        "weather_forecast", "Weather Forecast / Hazard",
                        "river_levels", "River Water Levels",
                        "earthquake", "Earthquakes",
                        "pollution", "Environmental Pollution"),
                "infrastructure", map(
                        "road_damage", "Road Conditions / Damage",
                        "bridge_damage", "Bridge Damage",
                        "airport_status", "Airport Status",
                        "railway_status", "Railway Availability",
                        "power_outage", "Power Outages",
                        "water_failure", "Water Supply Failures",
                        "communication_outage", "Communication Network Outages"),
                "logistics", map(
                        "fuel_stock", "Fuel Stock",
                        "food_stock", "Food Stock",
                        "medical_supplies", "Medical Supplies",
                        "vehicle_availability", "Vehicle Availability",
                        "helicopter_availability", "Helicopter Availability",
                        "warehouse_inventory", "Warehouse Inventory"),
                "population", map(
                        "population_movement", "Population Movement",
                        "internal_displacement", "Internal Displacement (IDP)",
                        "migration_trends", "Migration Trends",
                        "refugee_demographics", "Refugee Demographics",
                        "birth_death_stats", "Birth & Death Statistics")
        );
    }

    public static List<Map<String, Object>> modules() {
        return List.of(
                module(1, "population", "Population & Community Profile", "Population", "people-fill", "#0d3b66", "modules.population", "intelligence", "population"),
                module(2, "human_safety", "Human Safety Monitoring", "Human Safety", "shield-exclamation", "#dc2626", "modules.human_safety", "hybrid", "security"),
                module(3, "economic", "Economic Security Monitoring", "Economic", "currency-exchange", "#d97706", "modules.economic", "intelligence", "economic"),
                module(4, "health", "Health Security Monitoring", "Health", "heart-pulse", "#059669", "modules.health", "intelligence", "health"),
                module(5, "disaster", "Disaster & Climate Monitoring", "Disaster & Climate", "cloud-rain", "#0891b2", "modules.disaster", "intelligence", "environmental"),
                module(6, "border", "Border Management & Migration", "Border & Migration", "passport", "#1a6b9a", "modules.border", "operations", null),
                module(7, "infrastructure", "Infrastructure Monitoring", "Infrastructure", "building", "#64748b", "modules.infrastructure", "intelligence", "infrastructure"),
                module(8, "community", "Community Reporting", "Community Reports", "chat-left-quote", "#7c3aed", "modules.community", "dedicated", null),
                module(9, "capacity", "Institutional Capacity Monitoring", "Institutional Capacity", "person-badge", "#4338ca", "modules.capacity", "dedicated", null),
                module(10, "early_warning", "Early Warning Indicators", "Early Warning", "exclamation-octagon", "#ea580c", "modules.early_warning", "hybrid", null),
                module(11, "response", "Response Coordination", "Response", "people", "#be185d", "modules.response", "hybrid", null),
                module(12, "gis", "GIS & Spatial Information", "GIS & Spatial", "geo-alt-fill", "#0f766e", "modules.gis", "dedicated", null),
                module(13, "communications", "Public Communication", "Public Comms", "megaphone", "#0369a1", "modules.communications", "dedicated", null),
                module(14, "ai", "AI Analytics & Decision Support", "AI Analytics", "cpu", "#4f46e5", "modules.ai", "operations", null),
                module(15, "administration", "User & System Administration", "Administration", "gear-wide-connected", "#334155", "modules.administration", "operations", null),
                module(16, "dashboard_outputs", "Dashboard Outputs", "Dashboards", "speedometer2", "#0d3b66", "modules.dashboard", "operations", null)
        );
    }

    private static Map<String, Object> module(int number, String key, String label, String shortLabel,
                                              String icon, String color, String permission, String type, String intelDomain) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("key", key);
        m.put("number", number);
        m.put("label", label);
        m.put("short", shortLabel);
        m.put("icon", icon);
        m.put("color", color);
        m.put("permission", permission);
        m.put("type", type);
        if (intelDomain != null) {
            m.put("intelDomain", intelDomain);
        }
        return m;
    }

    private static Map<String, String> map(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }
}
