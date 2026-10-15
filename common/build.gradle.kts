plugins {
    id("com.possible-triangle.common")
}

common {
    //pinned so the build doesn't need to hit maven.neoforged.net to list versions
    neoformVersion = "26.1.2-1"
    accessWidener()
}

val moonlight_version: String by extra
val mc_version: String by extra
val jei_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")

    modCompileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
}
