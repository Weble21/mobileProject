package com.example.project

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class ApiSearch {
    val clientId = "rXM8xQ5wxI2VFq9pfkfv"
    val clientSecret = "phla5nF5_L"
    val listTitle = mutableListOf<String>()
    val listAdd = mutableListOf<String>()
    val listCategory = mutableListOf<String>()
    val listLoadAdd = mutableListOf<String>()
    val listLon = mutableListOf<Int>()
    val listLat = mutableListOf<Int>()

    fun main(currentLocation: String) {

        var text: String? = null
        try {
            text = URLEncoder.encode("${currentLocation} 대피시설", "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("검색어 인코딩 실패", e)
        }

        val apiURL = "https://openapi.naver.com/v1/search/local?query=" + text!! + "&display=5"    // json 결과

        val requestHeaders : HashMap<String, String> = HashMap()
        requestHeaders.put("X-Naver-Client-Id", clientId)
        requestHeaders.put("X-Naver-Client-Secret", clientSecret)
        val responseBody = get(apiURL, requestHeaders)

        return parseData(responseBody)

    }

    private operator fun get(apiUrl: String, requestHeaders: Map<String, String>): String {
        val con = connect(apiUrl)
        try {
            con.setRequestMethod("GET")
            for ((key, value) in requestHeaders) {
                con.setRequestProperty(key, value)
            }

            val responseCode = con.getResponseCode()
            return if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                readBody(con.getInputStream())
            } else { // 에러 발생
                readBody(con.getErrorStream())
            }

        } catch (e: IOException) {
            throw RuntimeException("API 요청과 응답 실패", e)
        } finally {
            con.disconnect()
        }
    }

    private fun connect(apiUrl: String): HttpURLConnection {
        try {
            val url = URL(apiUrl)
            return url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("연결이 실패했습니다. : $apiUrl", e)
        }

    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)

        try {
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()

                var line: String? = lineReader.readLine()
                while ( line != null) {
                    responseBody.append(line)
                    line = lineReader.readLine()
                }
                return responseBody.toString()
            }
        } catch (e: IOException) {
            throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }
    }
    fun removeHtmlTags(input: String): String {
        // 정규 표현식을 사용하여 HTML 태그 제거
        return input.replace(Regex("<.*?>"), "")
    }
    private fun parseData(responseBody: String) {
        var titles = try {
            val jsonObject = JSONObject(responseBody)
            val jsonArray = jsonObject.getJSONArray("items")
            Log.i("list", "$jsonArray")
            Log.i("개수", "${jsonArray.length()}")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val title = item.getString("title")
                if(title != "") {
                    val cleanTitle = removeHtmlTags(title)
                    listTitle.add(cleanTitle)
                }
                val category = item.getString("category")
                if(category != "") {
                    listCategory.add("$category")
                }

                val address = item.getString("address")
                if(address != "") {
                    listAdd.add("지번 주소: $address")
                }

                val roadAddress = item.getString("roadAddress")
                if(roadAddress != "") {
                    listLoadAdd.add("도로명 주소: $roadAddress")
                }

                val mapx = item.getInt("mapx")
                listLon.add(mapx)
                val mapy = item.getInt("mapy")
                listLat.add(mapy)


            }
        } catch (e: JSONException) {
            e.printStackTrace()
            emptyList<String>()
        }
    }


}