package com.example.hhoo7.popularview;

/*
* 自定义类，存放电影信息
* */
public class MovieData {
    /*
    * @param posterUri：电影海报uri
    * @param movieTitle：电影名称
    * @param overView：剧情简介
    * @param voteAverage：电影评分
    * @param releaseDate：发布日期
    * */
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
