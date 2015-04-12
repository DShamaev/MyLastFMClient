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
        String EVENT_ID = "eventId";

        @Column(type = Type.TEXT)
        String EVENT_TITLE = "eventTitle";

        @Column(type = Type.TEXT)
        String EVENT_DESCRIPTION = "eventDescription";

        @Column(type = Type.TEXT)
        String EVENT_ARTISTS = "eventArtists";

        @Column(type = Type.INTEGER)
        String EVENT_LOCATION = "eventLocation";

        @Column(type = Type.INTEGER)
        String EVENT_ATTENDANCE = "eventAttendance";

        @Column(type = Type.TEXT)
        String EVENT_IMG_URL = "eventImageUrl";

        //notify our view about changes in the table
        @URI(altNotify = EventsView.CONTENT_URI)
        String CONTENT_URI = "events";
    }

    @Table(LocationTable.TABLE_NAME)
    public static interface LocationTable extends BaseTable {

        String TABLE_NAME = "locations";

        @Column(type = Type.INTEGER)
        String LOCATION_ID = "locationId";

        @Column(type = Type.TEXT)
        String LOCATION_NAME = "locationName";

        @Column(type = Type.TEXT)
        String LOCATION_URL = "locationUrl";
    }

    @Table(TrackTable.TABLE_NAME)
    public static interface TrackTable extends BaseTable {

        String TABLE_NAME = "tracks";

        @Column(type = Type.TEXT)
        String TRACK_NAME = "trackName";

        @Column(type = Type.TEXT)
        String TRACK_ARTIST = "trackArtist";

        @Column(type = Type.TEXT)
        String TRACK_URL = "trackUrl";

        @Column(type = Type.INTEGER)
        String TRACK_DATE = "trackDate";

        @Column(type = Type.INTEGER)
        String TRACK_NOW_PLAYING = "nowPlaying";
    }

    /**
     * we want to calculate comments in post. we will use group by PostTable.ID
     */
    @SimpleView(EventsView.VIEW_NAME)
    public static interface EventsView {

        //VIEW_NAME - required field!!!
        String VIEW_NAME = "event_view";

        //no need to insert/delete/edit into view, only query :)
        @URI(onlyQuery = true)
        String CONTENT_URI = "event_view";

        @From(EventsTable.TABLE_NAME)
        String TABLE_POST = "post_table";
    }

}
