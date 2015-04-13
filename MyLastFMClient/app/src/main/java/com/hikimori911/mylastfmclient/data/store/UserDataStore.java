package com.hikimori911.mylastfmclient.data.store;

/**
 * Created by hikimori911 on 31.03.2015.
 */
import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.Providers;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.Autoincrement;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.Column.Type;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;
import com.hamsterksu.asql.projections.Projection;

@Providers({
        @Provider(name = "UserDataProvider", schemaClass = "UserDataSchema", authority = "com.hikimori911.mylastfmclient.AUTHORITY"),
})

@Schema(className = "UserDataSchema", dbName = "userdata.db", dbVersion = 1)
public interface UserDataStore {
    public static interface BaseTable {

        @PrimaryKey
        @Autoincrement
        @Column(type = Type.INTEGER)
        //@CursorType(int.class)
                String ID = "_id";

        @Projection
        String PROJECTION_ALL = "All";
    }

    @Table(EventsTable.TABLE_NAME)
    //@CursorWrapper
    public static interface EventsTable extends BaseTable {

        String TABLE_NAME = "events";

        @Column(type = Type.INTEGER)
        String EVENT_ID = "id";

        @Column(type = Type.TEXT)
        String EVENT_TITLE = "title";

        @Column(type = Type.TEXT)
        String EVENT_START_DATE = "startDate";

        @Column(type = Type.TEXT)
        String EVENT_DESCRIPTION = "eventDescription";

        @Column(type = Type.TEXT)
        String EVENT_ARTISTS = "eventArtists";

        @Column(type = Type.INTEGER)
        String EVENT_LOCATION = "venueId";

        @Column(type = Type.INTEGER)
        String EVENT_ATTENDANCE = "attendance";

        @Column(type = Type.TEXT)
        String EVENT_IMG_URL = "imageUrl";

        @Column(type = Type.INTEGER)
        String EVENT_CANCELLED = "cancelled";

        //notify our view about changes in the table
        @URI(altNotify = EventsView.CONTENT_URI)
        String CONTENT_URI = "events";
    }

    @Table(VenueTable.TABLE_NAME)
    public static interface VenueTable extends BaseTable {

        String TABLE_NAME = "venues";

        @Column(type = Type.INTEGER)
        String VENUE_ID = "id";

        @Column(type = Type.TEXT)
        String VENUE_NAME = "name";

        @Column(type = Type.TEXT)
        String VENUE_URL = "locationId";
    }

    @Table(LocationTable.TABLE_NAME)
    public static interface LocationTable extends BaseTable {

        String TABLE_NAME = "locations";

        @Column(type = Type.INTEGER)
        String LOCATION_CITY = "city";

        @Column(type = Type.TEXT)
        String LOCATION_COUNTRY = "country";

        @Column(type = Type.TEXT)
        String LOCATION_STREET = "street";
    }

    @Table(TrackTable.TABLE_NAME)
    public static interface TrackTable extends BaseTable {

        String TABLE_NAME = "tracks";

        @Column(type = Type.TEXT)
        String TRACK_NAME = "name";

        @Column(type = Type.TEXT)
        String TRACK_ARTIST = "trackArtist";

        @Column(type = Type.TEXT)
        String TRACK_URL = "url";

        @Column(type = Type.INTEGER)
        String TRACK_DATE = "date";

        @Column(type = Type.INTEGER)
        String TRACK_NOW_PLAYING = "nowPlaying";

        //notify our view about changes in the table
        @URI(altNotify = EventsView.CONTENT_URI)
        String CONTENT_URI = "events";
    }

    @SimpleView(EventsView.VIEW_NAME)
    public static interface EventsView {

        String VIEW_NAME = "event_view";

        @URI(onlyQuery = true)
        String CONTENT_URI = "event_view";

        @From(EventsTable.TABLE_NAME)
        String TABLE_EVENT = "event_table";
    }

    @SimpleView(TrackView.VIEW_NAME)
    public static interface TrackView {

        String VIEW_NAME = "track_view";

        @URI(onlyQuery = true)
        String CONTENT_URI = "track_view";

        @From(TrackTable.TABLE_NAME)
        String TABLE_TRACK = "track_table";
    }

}
