# Working with facebookAds Connector

## Overview

| Operation                                  | Description                            |
|--------------------------------------------|----------------------------------------|
| [createAd](#create-ad)                     | This is Create Ad operation.           |
| [createAdSet](#create-ad-set)              | This is Create Ad Set operation.       |
| [createCampaign](#create-campaign)         | This is Create campaign operation.     |
| [deleteAd](#delete-ad)                     | This is Delete Ad operation.           |
| [deleteAdSet](#delete-ad-set)              | This is Delete Ad Set operation.       |
| [deleteCampaign](#delete-campaign)         | This is Delete campaign operation.     |
| [dissociateCampaign](#dissociate-campaign) | This is Dissociate campaign operation. |
| [getAd](#get-ad)                           | This is Get ad operation.              |
| [getAdSet](#get-ad-set)                    | This is Get Ad Set operation.          |
| [getAdSets](#get-ad-sets)                  | This is Get Ad Sets operation.         |
| [getAds](#get-ads)                         | This is Get ads operation.             |
| [getCampaigns](#get-campaigns)             | This is Get campaigns operation.       |
| [updateAd](#update-ad)                     | This is Update Ad operation.           |
| [updateAdSet](#update-ad-set)              | This is Update Ad Set operation.       |
| [updateCampaign](#update-campaign)         | This is Update campaign operation.     |

## Operation Details

This section provides details on each of the operations.

### Create Ad

This is Create Ad operation.

**createAd**

```xml
<facebookAds.createAd>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.createAd>
```

**Properties**

* adAccountId: ID of the ad account.
* properties: Ad properties
* properties: Ad properties

### Create Ad Set

This is Create Ad Set operation.

**createAdSet**

```xml
<facebookAds.createAdSet>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.createAdSet>
```

**Properties**

* adAccountId: ID of the ad account.
* properties: Ad set properties
* properties: Ad set properties

### Create Campaign

This is Create campaign operation.

**createCampaign**

```xml
<facebookAds.createCampaign>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.createCampaign>
```

**Properties**

* adAccountId: ID of the ad account.
* properties: Campaign properties
* properties: Campaign properties

### Delete Ad

This is Delete Ad operation.

**deleteAd**

```xml
<facebookAds.deleteAd>
    <adId>{$ctx:adId}</adId>
</facebookAds.deleteAd>
```

**Properties**

* adId: ID of the ad

### Delete Ad Set

This is Delete Ad Set operation.

**deleteAdSet**

```xml
<facebookAds.deleteAdSet>
    <adSetId>{$ctx:adSetId}</adSetId>
</facebookAds.deleteAdSet>
```

**Properties**

* adSetId: ID of the ad set.

### Delete Campaign

This is Delete campaign operation.

**deleteCampaign**

```xml
<facebookAds.deleteCampaign>
    <campaignId>{$ctx:campaignId}</campaignId>
</facebookAds.deleteCampaign>
```

**Properties**

* campaignId: ID of the campaign.

### Dissociate Campaign

This is Dissociate campaign operation.

**dissociateCampaign**

```xml
<facebookAds.dissociateCampaign>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <beforeDate>{$ctx:beforeDate}</beforeDate>
    <deleteStrategy>{$ctx:deleteStrategy}</deleteStrategy>
    <objectCount>{$ctx:objectCount}</objectCount>
    <beforeDate>{$ctx:beforeDate}</beforeDate>
    <deleteStrategy>{$ctx:deleteStrategy}</deleteStrategy>
    <objectCount>{$ctx:objectCount}</objectCount>
</facebookAds.dissociateCampaign>
```

**Properties**

* adAccountId: ID of the ad account.
* beforeDate: Set a before date to delete campaigns before this date
* deleteStrategy: Delete strategy
* objectCount: Object count
* beforeDate: Set a before date to delete campaigns before this date
* deleteStrategy: Delete strategy
* objectCount: Object count

### Get Ad

This is Get ad operation.

**getAd**

```xml
<facebookAds.getAd>
    <adId>{$ctx:adId}</adId>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <updatedSince>{$ctx:updatedSince}</updatedSince>
    <fields>{$ctx:fields}</fields>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <updatedSince>{$ctx:updatedSince}</updatedSince>
    <fields>{$ctx:fields}</fields>
</facebookAds.getAd>
```

**Properties**

* adId: ID of the ad
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Date range used to aggregate insights metrics
* updatedSince: Updated since.
* fields: Fields of the campaign
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Date range used to aggregate insights metrics
* updatedSince: Updated since.
* fields: Fields of the campaign

### Get Ad Set

This is Get Ad Set operation.

**getAdSet**

```xml
<facebookAds.getAdSet>
    <adSetId>{$ctx:adSetId}</adSetId>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
</facebookAds.getAdSet>
```

**Properties**

* adSetId: ID of the ad set.
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Time Range. Note if time range is invalid, it will be ignored.
* fields: Fields of the ad set
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Time Range. Note if time range is invalid, it will be ignored.
* fields: Fields of the ad set

### Get Ad Sets

This is Get Ad Sets operation.

**getAdSets**

```xml
<facebookAds.getAdSets>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
    <datePreset>{$ctx:datePreset}</datePreset>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
</facebookAds.getAdSets>
```

**Properties**

* adAccountId: ID of the ad account.
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Time Range. Note if time range is invalid, it will be ignored.
* fields: Fields of the ad set
* datePreset: Predefined date range used to aggregate insights metrics.
* timeRange: Time Range. Note if time range is invalid, it will be ignored.
* fields: Fields of the ad set

### Get Ads

This is Get ads operation.

**getAds**

```xml
<facebookAds.getAds>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <datePreset>{$ctx:datePreset}</datePreset>
    <effectiveStatus>{$ctx:effectiveStatus}</effectiveStatus>
    <timeRange>{$ctx:timeRange}</timeRange>
    <updatedSince>{$ctx:updatedSince}</updatedSince>
    <fields>{$ctx:fields}</fields>
    <summary>{$ctx:summary}</summary>
    <datePreset>{$ctx:datePreset}</datePreset>
    <effectiveStatus>{$ctx:effectiveStatus}</effectiveStatus>
    <timeRange>{$ctx:timeRange}</timeRange>
    <updatedSince>{$ctx:updatedSince}</updatedSince>
    <fields>{$ctx:fields}</fields>
    <summary>{$ctx:summary}</summary>
</facebookAds.getAds>
```

**Properties**

* adAccountId: ID of the ad account.
* datePreset: Predefined date range used to aggregate insights metrics.
* effectiveStatus: Effective status for the ads
* timeRange: Date range used to aggregate insights metrics
* updatedSince: Updated since.
* fields: Fields of the campaign
* summary: Aggregated information about the edge, such as counts
* datePreset: Predefined date range used to aggregate insights metrics.
* effectiveStatus: Effective status for the ads
* timeRange: Date range used to aggregate insights metrics
* updatedSince: Updated since.
* fields: Fields of the campaign
* summary: Aggregated information about the edge, such as counts

### Get Campaigns

This is Get campaigns operation.

**getCampaigns**

```xml
<facebookAds.getCampaigns>
    <adAccountId>{$ctx:adAccountId}</adAccountId>
    <datePreset>{$ctx:datePreset}</datePreset>
    <effectiveStatus>{$ctx:effectiveStatus}</effectiveStatus>
    <isCompleted>{$ctx:isCompleted}</isCompleted>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
    <summary>{$ctx:summary}</summary>
    <datePreset>{$ctx:datePreset}</datePreset>
    <effectiveStatus>{$ctx:effectiveStatus}</effectiveStatus>
    <isCompleted>{$ctx:isCompleted}</isCompleted>
    <timeRange>{$ctx:timeRange}</timeRange>
    <fields>{$ctx:fields}</fields>
    <summary>{$ctx:summary}</summary>
</facebookAds.getCampaigns>
```

**Properties**

* adAccountId: ID of the ad account.
* datePreset: Predefined date range used to aggregate insights metrics.
* effectiveStatus: Effective status for the campaigns
* isCompleted: If true, we return completed campaigns.
* timeRange: Date range used to aggregate insights metrics
* fields: Fields of the campaign
* summary: Aggregated information about the edge, such as counts
* datePreset: Predefined date range used to aggregate insights metrics.
* effectiveStatus: Effective status for the campaigns
* isCompleted: If true, we return completed campaigns.
* timeRange: Date range used to aggregate insights metrics
* fields: Fields of the campaign
* summary: Aggregated information about the edge, such as counts

### Update Ad

This is Update Ad operation.

**updateAd**

```xml
<facebookAds.updateAd>
    <adId>{$ctx:adId}</adId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.updateAd>
```

**Properties**

* adId: ID of the ad
* properties: Ad set update properties
* properties: Ad set update properties

### Update Ad Set

This is Update Ad Set operation.

**updateAdSet**

```xml
<facebookAds.updateAdSet>
    <adSetId>{$ctx:adSetId}</adSetId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.updateAdSet>
```

**Properties**

* adSetId: ID of the ad set.
* properties: Ad set update properties
* properties: Ad set update properties

### Update Campaign

This is Update campaign operation.

**updateCampaign**

```xml
<facebookAds.updateCampaign>
    <campaignId>{$ctx:campaignId}</campaignId>
    <properties>{$ctx:properties}</properties>
    <properties>{$ctx:properties}</properties>
</facebookAds.updateCampaign>
```

**Properties**

* campaignId: ID of the campaign.
* properties: Campaign update properties
* properties: Campaign update properties
