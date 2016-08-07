package com.gellert.moviesforyou.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by gellert on 2016. 08. 04..
 */
public interface FavoritesColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @NotNull
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String IMAGE_URL = "url";

    @DataType(DataType.Type.TEXT) @NotNull
    String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    String RELEASE_DATE = "releaseDate";

    @DataType(DataType.Type.TEXT) @NotNull
    String PLOT_SYNOPSIS = "plotSynopsis";

    @DataType(DataType.Type.REAL) @NotNull
    String VOTE_AVERAGE = "voteAverage";
}
