package my.kelompok3.akuhadir.data.manager

import android.util.Log
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.UserProfile
import my.kelompok3.akuhadir.data.model.RoleData
import my.kelompok3.akuhadir.data.model.RoleType
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class RoleManager {
    private val supabase = SupabaseInstance.client

    // Mengambil data role untuk user tertentu berdasarkan id_user
    suspend fun getUserRoleByUserId(userId: Int): RoleData? {
        return try {
            val result = supabase.from("user_profile")
                .select(columns = Columns.list("id_user_profile, nama, nim, role, id_user")) {
                    filter {
                        eq("id_user", userId)
                    }
                }
                .decodeSingle<RoleData>()

            Log.d("RoleManager", "User Role fetched: ${result.nama}, Role: ${result.role}")
            result
        } catch (e: Exception) {
            Log.e("RoleManager", "Error fetching user role by user_id: ${e.message}", e)
            null
        }
    }

    // Mengambil data role untuk user tertentu berdasarkan id_user_profile
    suspend fun getUserRoleByProfileId(profileId: Int): RoleData? {
        return try {
            val result = supabase.from("user_profile")
                .select(columns = Columns.list("id_user_profile, nama, nim, role, id_user")) {
                    filter {
                        eq("id_user_profile", profileId)
                    }
                }
                .decodeSingle<RoleData>()

            Log.d("RoleManager", "User Role fetched: ${result.nama}, Role: ${result.role}")
            result
        } catch (e: Exception) {
            Log.e("RoleManager", "Error fetching user role by profile_id: ${e.message}", e)
            null
        }
    }

    // Mengambil semua user berdasarkan role tertentu
    suspend fun getUsersByRole(roleType: RoleType): List<RoleData> {
        return try {
            val result = supabase.from("user_profile")
                .select(columns = Columns.list("id_user_profile, nama, nim, role, id_user")) {
                    filter {
                        eq("role", roleType.value)
                    }
                }
                .decodeList<RoleData>()

            Log.d("RoleManager", "Users with role ${roleType.value}: ${result.size} users found")
            result
        } catch (e: Exception) {
            Log.e("RoleManager", "Error fetching users by role ${roleType.value}: ${e.message}", e)
            emptyList()
        }
    }

    // Cek apakah user memiliki role tertentu
    suspend fun hasRole(userId: Int, roleType: RoleType): Boolean {
        return try {
            val roleData = getUserRoleByUserId(userId)
            roleData?.role?.equals(roleType.value, ignoreCase = true) == true
        } catch (e: Exception) {
            Log.e("RoleManager", "Error checking role for user $userId: ${e.message}", e)
            false
        }
    }

    // Mengambil semua user dengan informasi role
    suspend fun getAllUsersWithRoles(): List<RoleData> {
        return try {
            val result = supabase.from("user_profile")
                .select(columns = Columns.list("id_user_profile, nama, nim, role, id_user"))
                .decodeList<RoleData>()

            Log.d("RoleManager", "All users with roles fetched: ${result.size} users")
            result
        } catch (e: Exception) {
            Log.e("RoleManager", "Error fetching all users with roles: ${e.message}", e)
            emptyList()
        }
    }

    // Update role user
    suspend fun updateUserRole(userId: Int, newRole: RoleType): Boolean {
        return try {
            supabase.from("user_profile")
                .update(mapOf("role" to newRole.value)) {
                    filter {
                        eq("id_user", userId)
                    }
                }

            Log.d("RoleManager", "User role updated successfully for user $userId to ${newRole.value}")
            true
        } catch (e: Exception) {
            Log.e("RoleManager", "Error updating user role: ${e.message}", e)
            false
        }
    }

    // Update role user berdasarkan profile_id
    suspend fun updateUserRoleByProfileId(profileId: Int, newRole: RoleType): Boolean {
        return try {
            supabase.from("user_profile")
                .update(mapOf("role" to newRole.value)) {
                    filter {
                        eq("id_user_profile", profileId)
                    }
                }

            Log.d("RoleManager", "User role updated successfully for profile $profileId to ${newRole.value}")
            true
        } catch (e: Exception) {
            Log.e("RoleManager", "Error updating user role by profile_id: ${e.message}", e)
            false
        }
    }

    // Utility function untuk mendapatkan role type dari string
    fun getRoleType(roleString: String): RoleType? {
        return RoleType.fromString(roleString)
    }

    // Utility function untuk mendapatkan role display name
    fun getRoleDisplayName(roleType: RoleType): String {
        return when (roleType) {
            RoleType.ANGGOTA -> "Anggota"
            RoleType.SEKRETARIS -> "Sekretaris"
            RoleType.PENGURUS -> "Pengurus"
        }
    }

    // Utility function untuk mendapatkan role display name dari string
    fun getRoleDisplayName(roleString: String): String {
        return getRoleType(roleString)?.let { getRoleDisplayName(it) } ?: "Unknown Role"
    }

    // Cek apakah user adalah anggota
    suspend fun isAnggota(userId: Int): Boolean {
        return hasRole(userId, RoleType.ANGGOTA)
    }

    // Cek apakah user adalah sekretaris
    suspend fun isSekretaris(userId: Int): Boolean {
        return hasRole(userId, RoleType.SEKRETARIS)
    }

    // Cek apakah user adalah pengurus
    suspend fun isPengurus(userId: Int): Boolean {
        return hasRole(userId, RoleType.PENGURUS)
    }

    // Mendapatkan semua anggota
    suspend fun getAllAnggota(): List<RoleData> {
        return getUsersByRole(RoleType.ANGGOTA)
    }

    // Mendapatkan semua sekretaris
    suspend fun getAllSekretaris(): List<RoleData> {
        return getUsersByRole(RoleType.SEKRETARIS)
    }

    // Mendapatkan semua pengurus
    suspend fun getAllPengurus(): List<RoleData> {
        return getUsersByRole(RoleType.PENGURUS)
    }

    // Mendapatkan statistik role
    suspend fun getRoleStatistics(): Map<String, Int> {
        return try {
            val allUsers = getAllUsersWithRoles()
            val stats = mutableMapOf<String, Int>()

            RoleType.values().forEach { roleType ->
                stats[getRoleDisplayName(roleType)] = allUsers.count {
                    it.role.equals(roleType.value, ignoreCase = true)
                }
            }

            Log.d("RoleManager", "Role statistics: $stats")
            stats
        } catch (e: Exception) {
            Log.e("RoleManager", "Error getting role statistics: ${e.message}", e)
            emptyMap()
        }
    }
}
