package mse.mobop.mymoviesbucketlists.ui.alrertdialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.dialog_movie_trailer.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.MovieVideoResult
import mse.mobop.mymoviesbucketlists.model.Video
import mse.mobop.mymoviesbucketlists.tmdapi.MovieApi
import mse.mobop.mymoviesbucketlists.tmdapi.MovieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("InflateParams", "SetTextI18n")
class DisplayMovieTrailerAlertDialog(
    context: Context,
    private val movieId: Int,
    layoutId: Int,
    private val autoPlayFirstVideo: Boolean = true,
    themeId: Int = R.style.DialogTheme
): AlertDialog(context, themeId){

    private var movieService = MovieApi.client!!.create(MovieService::class.java)
    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var videoPlayer: YouTubePlayer
    private lateinit var videos: List<Video>
    private var videoIndex = 0

    val view: View = LayoutInflater.from(context).inflate(layoutId, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        setView(view)

        youtubePlayerView = view.youtube_player_view

        movieService.getMovieTrailers(movieId)!!
            .enqueue(object: Callback<MovieVideoResult?> {
                override fun onResponse(
                    call: Call<MovieVideoResult?>,
                    response: Response<MovieVideoResult?>
                ) {
                    videos = response.body()!!.results!!
                    if (videos.isNullOrEmpty()) {
                        dismiss()
                        Toast.makeText(context, R.string.no_video, Toast.LENGTH_SHORT).show()
                    } else {
                        show()
                        youtubePlayerView.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                videoPlayer = youTubePlayer
                                setupNextPreviousVideoButtons(view)
                                loadVideo(view, autoPlayFirstVideo)
                            }

                            override fun onStateChange(
                                youTubePlayer: YouTubePlayer,
                                state: PlayerConstants.PlayerState
                            ) {
                                if (state == PlayerConstants.PlayerState.ENDED) {
                                    if (videos.size > 1) {
                                        videoIndex = (videoIndex + 1) % videos.size
                                        loadVideo(view)
                                    } else {
                                        youTubePlayer.seekTo(0f)
                                        youTubePlayer.pause()
                                    }
                                }
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<MovieVideoResult?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        super.onCreate(savedInstanceState)
    }

    override fun onDetachedFromWindow() {
        youtubePlayerView.release()
        super.onDetachedFromWindow()
    }

    private fun setupNextPreviousVideoButtons(view: View) {
        view.next_video.alpha = if (videos.size == 1) 0.4f else 1f
        view.previous_video.alpha = if (videos.size == 1) 0.4f else 1f
        if (videos.size > 1) {
            view.next_video.setOnClickListener {
                videoIndex = (videoIndex + 1) % videos.size
                loadVideo(view)
            }
            view.previous_video.setOnClickListener {
                videoIndex = (videoIndex - 1) % videos.size
                loadVideo(view)
            }
        }
    }

    private fun loadVideo(view: View, autoPlay: Boolean = true) {
        if (videos.size > 1) {
            view.video_type.text = "Video " + (videoIndex + 1) + " / " + videos.size +
                    " - " + videos[videoIndex].type
        } else {
            view.video_type.text = videos[videoIndex].type
        }
        view.video_title.text = videos[videoIndex].name
        if (autoPlay) {
            videoPlayer.loadVideo(videos[videoIndex].key!!, 0f)
        } else {
            videoPlayer.cueVideo(videos[videoIndex].key!!, 0f)
        }
    }
}