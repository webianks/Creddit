package com.webianks.creddit

import java.security.SecureRandom

/**
 * Created by ramankit on 25/9/17.
 */
class Util{

   companion object {
       fun randomString(characterSet: String, length: Int): String {
           val random = SecureRandom()
           val result = CharArray(length)
           for (i in result.indices) {
               // picks a random index out of character set > random character
               val randomCharIndex = random.nextInt(characterSet.length)
               result[i] = characterSet[randomCharIndex]
           }
           return String(result)
       }
   }
}