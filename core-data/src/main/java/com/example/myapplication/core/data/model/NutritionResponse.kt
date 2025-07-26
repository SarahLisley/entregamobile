package com.example.myapplication.core.data.model

import com.google.gson.annotations.SerializedName

// Modelos para a API Spoonacular
data class NutritionResponse(
    @SerializedName("results")
    val results: List<NutritionResult>?,
    @SerializedName("offset")
    val offset: Int?,
    @SerializedName("number")
    val number: Int?,
    @SerializedName("totalResults")
    val totalResults: Int?
)

data class NutritionResult(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("imageType")
    val imageType: String?,
    @SerializedName("nutrition")
    val nutrition: NutritionInfo?
)

data class NutritionInfo(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>?,
    @SerializedName("calories")
    val calories: Double?,
    @SerializedName("protein")
    val protein: String?,
    @SerializedName("fat")
    val fat: String?,
    @SerializedName("carbohydrates")
    val carbohydrates: String?
)

data class Nutrient(
    @SerializedName("name")
    val name: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("percentOfDailyNeeds")
    val percentOfDailyNeeds: Double?
) 