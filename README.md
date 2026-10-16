# Vacation

Fjölskylduferðaskipulagsappið. Fyrsta ferð: Búðardalur, júlí 2026.

An [ng-objects](https://github.com/ngobjects/ng-objects) app (ng-appserver + ng-adaptor-jetty, currently on the 0.1.6-SNAPSHOT development version — `mvn install` ng-objects first) with Cayenne on an in-memory H2 database, seeded on startup from the JSON datasets in `src/main/resources/ng/app/app-resources/`.

## Running

```
mvn package
```

Then run `./target/Vacation.woa/Vacation`, or import into Eclipse as a Maven project and launch `app.Application`.

The port defaults to 1200 (override with `-WOPort`), so: **http://localhost:1200**.

Templates live in `src/main/resources/ng/app/components/`, spot images in `src/main/resources/ng/app/webserver-resources/spots/` (served at `/nr/app/spots/…`).

## URLs

- `/` — front page: navigation, trip summary, embedded map
- `/map/` — full-page map of all spots (Leaflet + OpenStreetMap)
- `/calendar/` — day-by-day calendar for the trip (July 12th–19th 2026), events from `vacation.Events`
- `/spot/<slug>` — detail page for a spot
- `/drive/<slug>` — detail page for a driving route
- `/drive-geo/<slug>` — road-following GeoJSON geometry for a route

## Data

`spots.json` holds the spots (slug, name, category, lat/lon, description). `category` says what a place *is* (Náttúra, Laug, Safn…); the separate optional `status` field says how decided we are — `"status": "kannski"` renders the spot semi-transparent on maps and tagged "(kannski?)". Routes carry an ordered `spots` array of slugs — the stops along the way, listed on the route page and drawn on its map. The files are read once on startup (into the DB), so restart the app after editing them. **Coordinates are approximate** — good enough for the overview map, refine as needed.

`routes.json` holds driving-route metadata; `routes/<slug>.geojson` (also in app-resources) holds each route's road-following geometry, fetched **once** from the public OSRM API (`router.project-osrm.org`) and committed as a resource — nothing external is called at runtime. To add or change a route, pick waypoints (lon,lat pairs), fetch, and save:

```
curl "https://router.project-osrm.org/route/v1/driving/LON1,LAT1;LON2,LAT2?overview=full&geometries=geojson"
# wrap the result as {distanceKm, durationMin, geometry} in app-resources/routes/<slug>.geojson, add metadata to routes.json
```

## Next steps

- Category filtering on the map
- Trip entity + DB, so Búðardalur becomes vacation #1 of many
