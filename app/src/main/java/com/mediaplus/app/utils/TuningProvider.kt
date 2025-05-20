package com.mediaplus.app.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import com.mediaplus.app.R

/**
 * TuningProvider - Helper class to access XML-based UI tuning parameters
 *
 * This class provides methods to access the fine-tuning XML resources for each screen.
 * It allows app components to easily retrieve tuning values from the XML resources.
 */
class TuningProvider(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: TuningProvider? = null
        
        fun getInstance(context: Context): TuningProvider {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TuningProvider(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // Default color value for when a color is not set (transparent)
        private const val COLOR_TRANSPARENT = 0x00000000
    }
    
    private val resources: Resources = context.resources
    
    /**
     * Splash Screen tuning parameters
     */
    fun getSplashDuration(): Int = 
        resources.getInteger(R.integer.splash_duration_ms)
    
    fun getSplashLogoScaleFactor(): Float = 
        resources.getFloat(R.dimen.splash_logo_scale_factor)
    
    fun getSplashAnimationScaleFactor(): Float = 
        resources.getFloat(R.dimen.splash_animation_scale_factor)
    
    fun getSplashBackgroundColor(): Int = 
        resources.getColor(R.color.splash_background_color, null)
    
    fun getSplashTextColor(): Int = 
        resources.getColor(R.color.splash_text_color, null)
    
    fun getSplashAlpha(): Float = 
        resources.getFloat(R.dimen.splash_alpha)
    
    fun getSplashFadeInDuration(): Int = 
        resources.getInteger(R.integer.splash_fade_in_duration_ms)
    
    fun getSplashLogoBounceFactor(): Float = 
        resources.getFloat(R.dimen.splash_logo_bounce_factor)
    
    fun getSplashLogoPositionY(): Float = 
        resources.getFloat(R.dimen.splash_logo_position_y)
    
    /**
     * Main Screen tuning parameters
     */
    fun getMainNavbarHeightFactor(): Float = 
        resources.getFloat(R.dimen.main_navbar_height_factor)
    
    fun getMainNavbarIconScale(): Float = 
        resources.getFloat(R.dimen.main_navbar_icon_scale)
    
    fun getMainSectionTitleTextSize(): Float = 
        resources.getDimension(R.dimen.main_section_title_text_size)
    
    fun getMainContentSpacingFactor(): Float = 
        resources.getFloat(R.dimen.main_content_spacing_factor)
    
    fun getMainCardCornerRadius(): Float = 
        resources.getDimension(R.dimen.main_card_corner_radius)
    
    fun getMainFadeTransitionDuration(): Int = 
        resources.getInteger(R.integer.main_fade_transition_duration_ms)
    
    fun getMainContentPaddingTop(): Float = 
        resources.getDimension(R.dimen.main_content_padding_top)
    
    fun getMainContentPaddingBottom(): Float = 
        resources.getDimension(R.dimen.main_content_padding_bottom)
    
    fun getMainContentPaddingHorizontal(): Float = 
        resources.getDimension(R.dimen.main_content_padding_horizontal)
    
    fun getMainGridItemSpacing(): Float = 
        resources.getDimension(R.dimen.main_grid_item_spacing)
    
    fun getMainNavbarBackgroundColor(): Int? {
        val color = resources.getColor(R.color.main_navbar_background_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getMainNavbarSelectedIconColor(): Int? {
        val color = resources.getColor(R.color.main_navbar_selected_icon_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getMainNavbarUnselectedIconColor(): Int? {
        val color = resources.getColor(R.color.main_navbar_unselected_icon_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    /**
     * Video Player tuning parameters
     */
    fun getVideoControlScaleFactor(): Float = 
        resources.getFloat(R.dimen.video_control_scale_factor)
    
    fun getVideoControlSpacingFactor(): Float = 
        resources.getFloat(R.dimen.video_control_spacing_factor)
    
    fun getVideoSeekBarHeight(): Float = 
        resources.getDimension(R.dimen.video_seek_bar_height)
    
    fun getVideoControlOpacity(): Float = 
        resources.getFloat(R.dimen.video_control_opacity)
    
    fun getVideoControlShowDuration(): Int = 
        resources.getInteger(R.integer.video_control_show_duration_ms)
    
    fun getVideoControlFadeDuration(): Int = 
        resources.getInteger(R.integer.video_control_fade_duration_ms)
    
    fun getVideoDoubleTapSeekSeconds(): Int = 
        resources.getInteger(R.integer.video_double_tap_seek_seconds)
    
    fun getVideoControlBottomPadding(): Float = 
        resources.getDimension(R.dimen.video_control_bottom_padding)
    
    fun getVideoControlSidePadding(): Float = 
        resources.getDimension(R.dimen.video_control_side_padding)
    
    fun getVideoControlBackgroundColor(): Int? {
        val color = resources.getColor(R.color.video_control_background_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getVideoSeekBarPlayedColor(): Int? {
        val color = resources.getColor(R.color.video_seek_bar_played_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getVideoSeekBarBufferedColor(): Int? {
        val color = resources.getColor(R.color.video_seek_bar_buffered_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getVideoSeekBarUnplayedColor(): Int? {
        val color = resources.getColor(R.color.video_seek_bar_unplayed_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getVideoGestureSeekSensitivity(): Float = 
        resources.getFloat(R.dimen.video_gesture_seek_sensitivity)
    
    fun getVideoGestureVolumeSensitivity(): Float = 
        resources.getFloat(R.dimen.video_gesture_volume_sensitivity)
    
    fun getVideoGestureBrightnessSensitivity(): Float = 
        resources.getFloat(R.dimen.video_gesture_brightness_sensitivity)
    
    /**
     * Audio Player tuning parameters
     */
    fun getAudioAlbumArtSizeFactor(): Float = 
        resources.getFloat(R.dimen.audio_album_art_size_factor)
    
    fun getAudioControlScaleFactor(): Float = 
        resources.getFloat(R.dimen.audio_control_scale_factor)
    
    fun getAudioControlSpacingFactor(): Float = 
        resources.getFloat(R.dimen.audio_control_spacing_factor)
    
    fun getAudioSeekBarHeight(): Float = 
        resources.getDimension(R.dimen.audio_seek_bar_height)
    
    fun getAudioTitleTextSize(): Float = 
        resources.getDimension(R.dimen.audio_title_text_size)
    
    fun getAudioArtistTextSize(): Float = 
        resources.getDimension(R.dimen.audio_artist_text_size)
    
    fun getAudioTimeTextSize(): Float = 
        resources.getDimension(R.dimen.audio_time_text_size)
    
    fun getAudioAlbumArtCornerRadius(): Float = 
        resources.getDimension(R.dimen.audio_album_art_corner_radius)
    
    fun getAudioAlbumArtElevation(): Float = 
        resources.getDimension(R.dimen.audio_album_art_elevation)
    
    fun getAudioPlayerBackgroundColor(): Int? {
        val color = resources.getColor(R.color.audio_player_background_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioTitleTextColor(): Int? {
        val color = resources.getColor(R.color.audio_title_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioArtistTextColor(): Int? {
        val color = resources.getColor(R.color.audio_artist_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioTimeTextColor(): Int? {
        val color = resources.getColor(R.color.audio_time_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioSeekBarPlayedColor(): Int? {
        val color = resources.getColor(R.color.audio_seek_bar_played_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioSeekBarUnplayedColor(): Int? {
        val color = resources.getColor(R.color.audio_seek_bar_unplayed_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioControlTintColor(): Int? {
        val color = resources.getColor(R.color.audio_control_tint_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getAudioAlbumArtScaleDuringTransition(): Float = 
        resources.getFloat(R.dimen.audio_album_art_scale_during_transition)
    
    fun getAudioTransitionDuration(): Int = 
        resources.getInteger(R.integer.audio_transition_duration_ms)
    
    fun getAudioVisualizerSensitivity(): Float = 
        resources.getFloat(R.dimen.audio_visualizer_sensitivity)
    
    fun getAudioVisualizerHeightFactor(): Float = 
        resources.getFloat(R.dimen.audio_visualizer_height_factor)
    
    /**
     * Search Screen tuning parameters
     */
    fun getSearchBarCornerRadius(): Float = 
        resources.getDimension(R.dimen.search_bar_corner_radius)
    
    fun getSearchBarHeightFactor(): Float = 
        resources.getFloat(R.dimen.search_bar_height_factor)
    
    fun getSearchResultItemHeightFactor(): Float = 
        resources.getFloat(R.dimen.search_result_item_height_factor)
    
    fun getSearchTextSize(): Float = 
        resources.getDimension(R.dimen.search_text_size)
    
    fun getSearchResultTitleTextSize(): Float = 
        resources.getDimension(R.dimen.search_result_title_text_size)
    
    fun getSearchResultSubtitleTextSize(): Float = 
        resources.getDimension(R.dimen.search_result_subtitle_text_size)
    
    fun getSearchKeyboardAnimationDuration(): Int = 
        resources.getInteger(R.integer.search_keyboard_animation_duration_ms)
    
    fun getSearchBarHorizontalMargin(): Float = 
        resources.getDimension(R.dimen.search_bar_horizontal_margin)
    
    fun getSearchBarTopMargin(): Float = 
        resources.getDimension(R.dimen.search_bar_top_margin)
    
    fun getSearchResultsPadding(): Float = 
        resources.getDimension(R.dimen.search_results_padding)
    
    fun getSearchResultItemSpacing(): Float = 
        resources.getDimension(R.dimen.search_result_item_spacing)
    
    fun getSearchBarBackgroundColor(): Int? {
        val color = resources.getColor(R.color.search_bar_background_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchBarIconColor(): Int? {
        val color = resources.getColor(R.color.search_bar_icon_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchTextColor(): Int? {
        val color = resources.getColor(R.color.search_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchHintColor(): Int? {
        val color = resources.getColor(R.color.search_hint_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchResultTitleTextColor(): Int? {
        val color = resources.getColor(R.color.search_result_title_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchResultSubtitleTextColor(): Int? {
        val color = resources.getColor(R.color.search_result_subtitle_text_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun getSearchResultItemBackgroundColor(): Int? {
        val color = resources.getColor(R.color.search_result_item_background_color, null)
        return if (color == COLOR_TRANSPARENT) null else color
    }
    
    fun isSearchBarAnimationEnabled(): Boolean = 
        resources.getBoolean(R.bool.search_enable_search_bar_animation)
    
    fun getSearchResultFadeInDuration(): Int = 
        resources.getInteger(R.integer.search_result_fade_in_duration_ms)
    
    fun getSearchSuggestionDelay(): Int = 
        resources.getInteger(R.integer.search_suggestion_delay_ms)
    
    /**
     * Home Screen tuning parameters
     */
    fun getHomeCardWidth(): Float = resources.getDimension(R.dimen.home_card_width)
    fun getHomeCardMinWidth(): Float = resources.getDimension(R.dimen.home_card_min_width)
    fun getHomeCardMaxWidth(): Float = resources.getDimension(R.dimen.home_card_max_width)
    fun getHomeCardHorizontalSpacing(): Float = resources.getDimension(R.dimen.home_card_horizontal_spacing)
    fun getHomeCardHorizontalPadding(): Float = resources.getDimension(R.dimen.home_card_horizontal_padding)
    fun getHomeCardAspectRatio(): Float = resources.getFloat(R.dimen.home_card_aspect_ratio)
    fun getHomeCardElevation(): Float = resources.getDimension(R.dimen.home_card_elevation)
    fun getHomeCardBackgroundColor(): Int = resources.getColor(R.color.home_card_background_color, null)
    fun getHomeCardShadowColor(): Int = resources.getColor(R.color.home_card_shadow_color, null)
    fun getHomeCardBorderWidth(): Float = resources.getDimension(R.dimen.home_card_border_width)
    fun getHomeCardBorderColor(): Int = resources.getColor(R.color.home_card_border_color, null)
    fun getHomeSectionTitleColor(): Int = resources.getColor(R.color.home_section_title_color, null)
    fun getHomeSectionTitleFontWeight(): Int = resources.getInteger(R.dimen.home_section_title_font_weight)
}
