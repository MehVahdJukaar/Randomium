plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val codecui_version: String by extra
val mc_version: String by extra
val jei_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    modRuntimeOnly("net.mehvahdjukaar:codecui-neoforge:${codecui_version}")

    modCompileOnly("mezz.jei:jei-${mc_version}-neoforge-api:${jei_version}")
    modRuntimeOnly("mezz.jei:jei-${mc_version}-neoforge:${jei_version}")
}
