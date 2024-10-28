package util

sealed interface AppError : Error {
   enum class NetworkError : AppError {
      REQUEST_TIMEOUT,
      UNAUTHORIZED,
      CONFLICT,
      TOO_MANY_REQUESTS,
      NO_INTERNET,
      PAYLOAD_TOO_LARGE,
      SERVER_ERROR,
      SERIALIZATION,
      UNKNOWN;
   }

   enum class DatabaseError : AppError {
      UNKNOWN
   }
}