# Vacation

Fjölskylduferðaskipulagsappið. Fyrsta ferð: Búðardalur, júlí 2026.

A plain WebObjects app (wonder-slim ERExtensions + AjaxSlim + wo-adaptor-jetty), modelled on the Hugi project but without any database — spot data lives in `src/main/woresources/spots.json` and will migrate to a DB later.

## Running

```
mvn package
```

Or import into Eclipse (WOLips incremental builder is configured in `.project`) and launch `app.AppLauncher`.

The port comes from `~/WebObjects.properties` (1200 on this machine) or `-WOPort`, so: **http://localhost:1200**.

## URLs

- `/` — front page: navigation, trip summary, embedded map
- `/map/` — full-page map of all spots (Leaflet + OpenStreetMap)
- `/calendar/` — day-by-day calendar for the trip (July 12th–19th 2026), events from `vacation.Events`
- `/spot/<slug>` — detail page for a spot
- `/route/<slug>` — detail page for a driving route
- `/route-geo/<slug>` — road-following GeoJSON geometry for a route

## Data

`spots.json` holds the spots (slug, name, category, lat/lon, description). In development mode the file is reloaded on every request, so it can be edited while the app runs. **Coordinates are approximate** — good enough for the overview map, refine as needed.

`routes.json` holds driving-route metadata; `routes/<slug>.geojson` holds each route's road-following geometry, fetched **once** from the public OSRM API (`router.project-osrm.org`) and committed as a resource — nothing external is called at runtime. To add or change a route, pick waypoints (lon,lat pairs), fetch, and save:

```
curl "https://router.project-osrm.org/route/v1/driving/LON1,LAT1;LON2,LAT2?overview=full&geometries=geojson"
# wrap the result as {distanceKm, durationMin, geometry} in routes/<slug>.geojson, add metadata to routes.json
```

## Next steps

- Category filtering on the map (AjaxSlim)
- Trip entity + DB, so Búðardalur becomes vacation #1 of many
