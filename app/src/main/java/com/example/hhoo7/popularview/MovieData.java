package com.example.hhoo7.popularview;

/*
* 自定义类.
* */
public class MovieData {
    private String posterUri;
    private String movieTitle;
    private String overView;
    private String voteAverage;
    private String releaseDate;

    public MovieData(String posterUri, String movieTitle, String overView, String voteAverage, String releaseDate) {
        this.posterUri = posterUri;
        this.movieTitle = movieTitle;
        this.overView = overView;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getPosterUri() {
        return posterUri;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getOverView() {
        return overView;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "posterUri='" + posterUri + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", overView='" + overView + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
