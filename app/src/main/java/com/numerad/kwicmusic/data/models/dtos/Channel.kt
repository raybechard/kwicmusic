package com.numerad.kwicmusic.data.models.dtos

data class Channel(
    val kind: String,
    var etag: String,
    var id: String,
    var snippet: SnippetChannel,
    var contentDetails: ContentDetailsChannel,
    var statistics: Statistics,
    var topicDetails: TopicDetails,
    var status: StatusChannel,
    var brandingSettings: BrandingStatus,
    var invideoPromotion: InVideoPromotion,
    var auditDetails: AuditDetails,
    var contentOwnerDetails: ContentOwnerDetails,
    var localizations: Map<String, Local> = mapOf()
)

data class SnippetChannel(
    var title: String,
    var description: String,
    var customUrl: String,
    var publishedAt: String,
    var thumbnails: Map<String, Thumbnail> = mapOf(),
    var defaultLanguage: String,
    var localized: Local,
    var country: String
)

data class ContentOwnerDetails(
    var contentOwner: String,
    var timeLinked: String
)

class AuditDetails {}
class InVideoPromotion {}
class BrandingStatus {}
class StatusChannel {}
class TopicDetails {}
class Statistics {}
class ContentDetailsChannel {}