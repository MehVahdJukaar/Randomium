plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val codecui_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    modRuntimeOnly("net.mehvahdjukaar:codecui-neoforge:${codecui_version}")

    modImplementation("curse.maven:jei-238222:5846880")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:16.0.777")
    modCompileOnly("curse.maven:emi-580555:5704405")
}
