package com.numerad.kwicmusic.data.models.dtos

data class Video(
    val kind: String,
    var etag: String,
    var id: String,
    var snippet: SnippetVideo,
    var contentDetails: ContentDetailsVideo,
    var status: StatusChannel,
    var statistics: Statistics,
    var player: Player,
    var topicDetails: TopicDetailsVideo,
    var recordingDetails: RecordingDetails,
    var fileDetails: FileDetails,
    var processingDetails: ProcessingDetails,
    var suggestions: Suggestions,
    var liveStreamingDetails: LiveStreamingDetails,
    var localizations: Map<String, Local> = mapOf()
)

class ContentDetailsVideo(
    var duration: String
)

class SnippetVideo(
    var channelTitle: String
)

class Suggestions {}
class LiveStreamingDetails {}
class ProcessingDetails {}
class FileDetails {}
class RecordingDetails {}
class TopicDetailsVideo {}