package com.example.data

data class GasComponent(
    val formula: String,
    val name: String,
    val percentage: Double
)

data class AtmosphereProperties(
    val mainComponents: List<GasComponent>,
    val densityDescription: String,
    val temperatureProfile: String,
    val visualAtmosphereColor: String, // Hex color for glow
    val uniqueFeature: String,
    val ozoneLayerPresence: Boolean,
    val atmosphericPressureBar: Double
)

data class SimulatedOrbitData(
    val semiMajorAxisAU: Double,
    val eccentricity: Double,
    val orbitalPeriodDays: Double,
    val meanVelocityKms: Double,
    val axialTiltDegrees: Double,
    val inclinationDegrees: Double
)

data class KidsProfile(
    val nickname: String,
    val cartoonQuote: String,
    val funFact: String,
    val simpleAtmosphere: String,
    val gravityComparison: String, // e.g. "If you jump here, you'd fly like a superhero!"
    val miniActivity: String // A simple fun question/prompt
)

data class AdultsProfile(
    val chemistryFormulas: String,
    val technicalSummary: String,
    val discoveryRecord: String,
    val discoveryYear: String,
    val significantMissions: List<String>,
    val escapeVelocityKms: Double,
    val densityGcm3: Double,
    val massMultiplier: Double // Relative to Earth
)

data class Planet(
    val id: String,
    val name: String,
    val orderFromSun: Int,
    val colorHex: String,
    val ringColorHex: String? = null,
    val radiusKm: Double,
    val hasRings: Boolean,
    val moonsCount: Int,
    val atmosphereProperties: AtmosphereProperties,
    val orbitData: SimulatedOrbitData,
    val kidsProfile: KidsProfile,
    val adultsProfile: AdultsProfile
)

object CelestialDatabase {
    val Planets = listOf(
        Planet(
            id = "mercury",
            name = "Mercury",
            orderFromSun = 1,
            colorHex = "#9E9E9E",
            radiusKm = 2439.7,
            hasRings = false,
            moonsCount = 0,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("O2", "Oxygen", 42.0),
                    GasComponent("Na", "Sodium", 29.0),
                    GasComponent("H2", "Hydrogen", 22.0),
                    GasComponent("He", "Helium", 6.0)
                ),
                densityDescription = "Virtually non-existent exosphere. Extremely thin.",
                temperatureProfile = "Wild swings from scorching -180°C at night to extreme 430°C in daylight.",
                visualAtmosphereColor = "#40FFFFFF",
                uniqueFeature = "Solar wind directly blasts the rocky, cratered surface, blasting away gases.",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 1e-14
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 0.387,
                eccentricity = 0.2056,
                orbitalPeriodDays = 87.97,
                meanVelocityKms = 47.36,
                axialTiltDegrees = 0.034,
                inclinationDegrees = 7.0
            ),
            kidsProfile = KidsProfile(
                nickname = "The Speedy Rock 🏃✨",
                cartoonQuote = "I'm the closest planet to the Sun! I run around it in just 88 days!",
                funFact = "Even though I'm closest to the Sun, I'm NOT the hottest planet! Venus is actually hotter because of its thick greenhouse blanket. I get super hot in the day but freeeeze at night! 🥶🔥",
                simpleAtmosphere = "Almost zero air! I wear a super thin shield of helium and oxygen gases.",
                gravityComparison = "Low gravity! You could jump nearly 3 times higher on me than on Earth!",
                miniActivity = "Imagine playing hide-and-seek here! Would you hide in a deep crater, or run super fast to avoid the blazing sun?"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "O₂, Na, H₂, He",
                technicalSummary = "Mercury possesses a highly tenuous exosphere rather than a stable atmosphere. Heavy particles are maintained by planetary sputtering of silicate crusts under continuous Solar Wind bombardment. Due to the lack of heat-retaining atmospheric buffers, the planet experiences the most severe diurnal thermal fluctuations in the Solar System.",
                discoveryRecord = "Known since ancient Babylonian times. First detailed astrological tracking.",
                discoveryYear = "Ancient Times",
                significantMissions = listOf("Mariner 10 (1974-1975)", "MESSENGER (2008-2015)", "BepiColombo (Launched 2018)"),
                escapeVelocityKms = 4.3,
                densityGcm3 = 5.427,
                massMultiplier = 0.055
            )
        ),
        Planet(
            id = "venus",
            name = "Venus",
            orderFromSun = 2,
            colorHex = "#E5A93B",
            radiusKm = 6051.8,
            hasRings = false,
            moonsCount = 0,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("CO2", "Carbon Dioxide", 96.5),
                    GasComponent("N2", "Nitrogen", 3.5),
                    GasComponent("SO2", "Sulfur Dioxide", 0.015)
                ),
                densityDescription = "Super massive cloud layers. Atmospheric mass is 93x Earth's.",
                temperatureProfile = "Constant thermal runaway, averaging 465°C - hot enough to melt lead!",
                visualAtmosphereColor = "#CCF1B631",
                uniqueFeature = "Perpetual clouds of lethal sulphuric acid droplets that reflect sunlight.",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 92.0
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 0.723,
                eccentricity = 0.0067,
                orbitalPeriodDays = 224.7,
                meanVelocityKms = 35.02,
                axialTiltDegrees = 177.3,
                inclinationDegrees = 3.39
            ),
            kidsProfile = KidsProfile(
                nickname = "The Glowing Hot-Pot 🌋💛",
                cartoonQuote = "I am the brightest star-like object in your sky, wrapped in bright yellow clouds!",
                funFact = "It rains acid here, and my air is so heavy and thick it would feel like swimming through pudding! Also, I spin backwards compared to almost all other planets! 🔄🤪",
                simpleAtmosphere = "A super thick heat-trapping CO2 blanket with yellow toxic clouds.",
                gravityComparison = "Almost the same as Earth! You would feel almost completely normal walking here.",
                miniActivity = "We need a super-shield space umbrella to survive acid rain! If you were building one, what color would it be?"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "CO₂, N₂, SO₂",
                technicalSummary = "Venus is a textbook example of a runaway greenhouse effect. The hyperdevelopment of CO₂ pressure drives temperatures far past radiative equilibrium, maintaining a global isotherm regardless of solar latitude or diurnal division. The atmospheric dynamics are dominated by wind 'superrotation' - wrapping the entire planet every 4 Earth days.",
                discoveryRecord = "Recorded by early Mayan, Babylonian astronomers as the Morning/Evening star.",
                discoveryYear = "Ancient Times",
                significantMissions = listOf("Venera 7 (1970 - First lander)", "Pioneer Venus (1978)", "Magellan (1990-1994)", "Akatsuki (2015-Present)"),
                escapeVelocityKms = 10.36,
                densityGcm3 = 5.243,
                massMultiplier = 0.815
            )
        ),
        Planet(
            id = "earth",
            name = "Earth",
            orderFromSun = 3,
            colorHex = "#2B66E2",
            radiusKm = 6371.0,
            hasRings = false,
            moonsCount = 1,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("N2", "Nitrogen", 78.08),
                    GasComponent("O2", "Oxygen", 20.95),
                    GasComponent("Ar", "Argon", 0.93),
                    GasComponent("CO2", "Carbon Dioxide", 0.04)
                ),
                densityDescription = "Perfectly balanced troposphere, stratosphere, mesosphere, and thermosphere.",
                temperatureProfile = "Moderate, ranging from -89°C in Antarctica to 58°C in hot deserts.",
                visualAtmosphereColor = "#4D3B82F6",
                uniqueFeature = "Liquid water stability, highly active oxygen-nitrogen cycle, and protective ozone.",
                ozoneLayerPresence = true,
                atmosphericPressureBar = 1.013
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 1.0,
                eccentricity = 0.0167,
                orbitalPeriodDays = 365.25,
                meanVelocityKms = 29.78,
                axialTiltDegrees = 23.44,
                inclinationDegrees = 0.0
            ),
            kidsProfile = KidsProfile(
                nickname = "Our Precious Blue Marble 🌎💙",
                cartoonQuote = "I am the only planet known to host amazing life, rich oceans, and puffy clouds!",
                funFact = "Over 70% of my body is covered in liquid water, which gives me my beautiful blue appearance from space! My atmosphere acts like a cozy sun-shield and spaceship shield! 🛡️🌠",
                simpleAtmosphere = "The perfect life blend: Nitrogen and Oxygen with a protective ozone shield.",
                gravityComparison = "This is your home gravity scale! 100% familiar.",
                miniActivity = "Look outside! Can you feel the breeze? That is Earth's atmospheric blanket moving around. Draw a cloud shape!"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "N₂, O₂, Ar, H₂O",
                technicalSummary = "The terrestrial planet atmosphere is unique in its biological oxygenation. Maintained by biological photosynthesis, the thermodynamic disequilibrium enables life. Radiative balancing is regulated by water vapor, methane, and CO₂. Magnetospheric coupling with a robust core dynamo blocks solar wind ablation.",
                discoveryRecord = "Humanity's homeworld. Scientifically characterized across millennia.",
                discoveryYear = "Prehistoric",
                significantMissions = listOf("Apollo Missions", "Sputnik (1957)", "Hubble Space Telescope", "International Space Station"),
                escapeVelocityKms = 11.19,
                densityGcm3 = 5.515,
                massMultiplier = 1.0
            )
        ),
        Planet(
            id = "mars",
            name = "Mars",
            orderFromSun = 4,
            colorHex = "#D14F35",
            radiusKm = 3389.5,
            hasRings = false,
            moonsCount = 2,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("CO2", "Carbon Dioxide", 95.3),
                    GasComponent("N2", "Nitrogen", 1.9),
                    GasComponent("Ar", "Argon", 1.9)
                ),
                densityDescription = "Thin with surface pressure only 1/130th (0.6%) of Earth's.",
                temperatureProfile = "Chilly, averaging -62°C, dipping down to -140°C during polar winter night.",
                visualAtmosphereColor = "#33FF7D54",
                uniqueFeature = "Global dust storms that saturate the sky with fine iron oxide particles.",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 0.006
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 1.524,
                eccentricity = 0.0934,
                orbitalPeriodDays = 686.98,
                meanVelocityKms = 24.08,
                axialTiltDegrees = 25.19,
                inclinationDegrees = 1.85
            ),
            kidsProfile = KidsProfile(
                nickname = "The Rusty Red Sandbox 🔴🤖",
                cartoonQuote = "I'm covered in rusted iron dust, which makes me red! Rovers roll all over me!",
                funFact = "I have the tallest volcano in the entire solar system! It is called Olympus Mons, and it is 3 times taller than Mount Everest! Imagine climbing THAT! 🌋🚀",
                simpleAtmosphere = "Super thin carbon dioxide air. It gets very cold here!",
                gravityComparison = "Low gravity! You weigh only 38% of your weight. You would jump super high!",
                miniActivity = "We have many robotic rovers exploring my red dirt. Use your finger to navigate a rover across your screen!"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "CO₂, N₂, Ar",
                technicalSummary = "Mars hosts an extremely low-density CO₂ atmosphere. Carbon dioxide condenses into polar dry ice deposits seasonally, leading to annual barometric variations of up to 30%. Deprived of a global dipole magnetic field, solar wind ion excavation remains active, slowly stripping volatiles. Water ice vapor clouds form in mid-latitudes.",
                discoveryRecord = "Recorded by ancient Egyptians as 'Her Desher' (The Red One). Galileo observed in 1610.",
                discoveryYear = "Ancient Times",
                significantMissions = listOf("Viking 1 & 2 (1976)", "Opportunity & Spirit (2004)", "Curiosity (2012-Present)", "Perseverance & Ingenuity (2021-Present)"),
                escapeVelocityKms = 5.03,
                densityGcm3 = 3.933,
                massMultiplier = 0.107
            )
        ),
        Planet(
            id = "jupiter",
            name = "Jupiter",
            orderFromSun = 5,
            colorHex = "#D4A373",
            radiusKm = 69911.0,
            hasRings = true,
            ringColorHex = "#22FFFFFF",
            moonsCount = 95,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("H2", "Hydrogen", 89.8),
                    GasComponent("He", "Helium", 10.2),
                    GasComponent("CH4", "Methane", 0.3)
                ),
                densityDescription = "Gas giant. Gradual density transition from atmospheric gas to metallic hydrogen.",
                temperatureProfile = "Brutally cold -108°C at outer clouds, but hot as the Sun's surface at the metallic core.",
                visualAtmosphereColor = "#4DEAA374",
                uniqueFeature = "The 'Great Red Spot' - a monstrous anticyclonic storm wider than Earth, roaring for centuries.",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 1000.0 // Effective level
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 5.203,
                eccentricity = 0.0489,
                orbitalPeriodDays = 4332.59,
                meanVelocityKms = 13.07,
                axialTiltDegrees = 3.13,
                inclinationDegrees = 1.30
            ),
            kidsProfile = KidsProfile(
                nickname = "The King of Stars 👑🌀",
                cartoonQuote = "I'm the biggest, heaviest giant in the solar system! I could swallow all other planets combined!",
                funFact = "I am a giant ball of gas! If you tried to land on me, you wouldn't find any solid ground, just thicker and thicker clouds until you got squished! I spin super fast, making days just 10 hours! ⏱️💨",
                simpleAtmosphere = "Heavy gas blanket made of hydrogen and helium, matching stars.",
                gravityComparison = "Huge gravity! You would weigh 2.5x more. Jumping would be super hard!",
                miniActivity = "My Great Red Spot is a giant tornado that has lasted for hundreds of years! Draw a whirlpool circle in the air!"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "H₂, He, CH₄",
                technicalSummary = "Jupiter's colossal hydrogen-helium atmosphere behaves as a supercritical fluid under deep planetary pressure gradients. Horizontal wind profiles are organized into counter-rotating zonal bands (belts and zones) fueled by deep thermal convection. The extreme Jovian magnetosphere traps high-energy electrons, producing brutal radiation rings.",
                discoveryRecord = "Recorded by early Babylonian, Chinese observers. Galileo discovered 4 main moons in 1610.",
                discoveryYear = "Ancient Times",
                significantMissions = listOf("Voyager 1 & 2 (1979)", "Galileo Probe (1995-2003)", "Juno (2016-Present)", "JUICE (En Route)"),
                escapeVelocityKms = 59.5,
                densityGcm3 = 1.326,
                massMultiplier = 317.8
            )
        ),
        Planet(
            id = "saturn",
            name = "Saturn",
            orderFromSun = 6,
            colorHex = "#EAD59E",
            ringColorHex = "#A6D2B392",
            radiusKm = 58232.0,
            hasRings = true,
            moonsCount = 146,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("H2", "Hydrogen", 96.3),
                    GasComponent("He", "Helium", 3.2),
                    GasComponent("CH4", "Methane", 0.4)
                ),
                densityDescription = "Less dense than water. Extremely expanded planetary volume.",
                temperatureProfile = "Cloud deck temperature averages -139°C. Deep pressures increase quickly.",
                visualAtmosphereColor = "#4DE4BA6A",
                uniqueFeature = "Breathtaking orbital ring complex composed of pure water ice chunks and carbon dust.",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 1000.0
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 9.537,
                eccentricity = 0.0541,
                orbitalPeriodDays = 10759.22,
                meanVelocityKms = 9.69,
                axialTiltDegrees = 26.73,
                inclinationDegrees = 2.48
            ),
            kidsProfile = KidsProfile(
                nickname = "The Master of Rings 🪐💖",
                cartoonQuote = "Check out my icy crown! My rings span wide enough to make a cosmic slide!",
                funFact = "I am so light and puffy because I'm made of gas. If you found a bathtub big enough for me, I would FLOAT like a rubber ducky! 🛁🦆 Yes, really!",
                simpleAtmosphere = "Super puffy cosmic gas made of hydrogen, helium, and sweet ammonia crystals.",
                gravityComparison = "Unbelievably, despite my massive size, your weight here is almost the same as on Earth!",
                miniActivity = "My rings are made of trillions of icy snowballs. Imagine throwing a snowball in space! How far would it float?"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "H₂, He, CH₄",
                technicalSummary = "Saturn's interior density is lower than any stellar or planetary counterpart. High altitude atmospheres show ammonia crystal clouds overlaying ammonium hydrosulfide layers. Wind velocities are exceptionally high, pushing jet stream speeds up to 1,800 km/h. Seasonal storm dynamics, such as the Great White Spots, occur every Saturnian year.",
                discoveryRecord = "Observed by ancient civilizations. Rings first spotted as 'ears' by Galileo in 1610, updated by Huygens.",
                discoveryYear = "Ancient Times",
                significantMissions = listOf("Pioneer 11 (1979)", "Voyager 1 & 2 (1980)", "Cassini-Huygens (2004-2017)"),
                escapeVelocityKms = 35.5,
                densityGcm3 = 0.687,
                massMultiplier = 95.2
            )
        ),
        Planet(
            id = "uranus",
            name = "Uranus",
            orderFromSun = 7,
            colorHex = "#A6E2E1",
            ringColorHex = "#3390FF24",
            radiusKm = 25362.0,
            hasRings = true,
            moonsCount = 28,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("H2", "Hydrogen", 82.5),
                    GasComponent("He", "Helium", 15.2),
                    GasComponent("CH4", "Methane", 2.3)
                ),
                densityDescription = "Methane concentrations convert sunlight into beautiful cyan reflections.",
                temperatureProfile = "The coldest atmospheric thermal profile, bottoming out at -224°C.",
                visualAtmosphereColor = "#4D76E2E1",
                uniqueFeature = "Extreme tilt of 98 degrees. Uranus effectively orbits the Sun rolling on its side!",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 1000.0
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 19.191,
                eccentricity = 0.0472,
                orbitalPeriodDays = 30685.4,
                meanVelocityKms = 6.81,
                axialTiltDegrees = 97.77,
                inclinationDegrees = 0.77
            ),
            kidsProfile = KidsProfile(
                nickname = "The Lazy Ice Roller ❄️💤",
                cartoonQuote = "I rolling-slide on my side! I've also got smelly hydrogen sulfide clouds like rotten eggs!",
                funFact = "My winters last 21 years because I orbit tilted fully on my side! My methane gas absorbs red light and sends out beautiful blue-aquamarine waves! 🧊💙",
                simpleAtmosphere = "Chilly ice-gas layer rich in water, ammonia and methane smells.",
                gravityComparison = "Slightly less than Earth! You would feel about 10% lighter here.",
                miniActivity = "A 21-year winter! Imagine how long you could sleep on a snowy day. Draw a snowman floating in the dark!"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "H₂, He, CH₄",
                technicalSummary = "Uranus, categorized as an 'Ice Giant', has an atmosphere dominated by tropospheric methane absorption spectral lines. The extreme axial obliquity causes unique seasonal solar heating cycles, yet its active meteorology remains surprisingly low in dynamic energy compared to Neptune.",
                discoveryRecord = "Discovered by William Herschel using a handcrafted telescope.",
                discoveryYear = "1781",
                significantMissions = listOf("Voyager 2 (1986 flyby)"),
                escapeVelocityKms = 21.3,
                densityGcm3 = 1.27,
                massMultiplier = 14.54
            )
        ),
        Planet(
            id = "neptune",
            name = "Neptune",
            orderFromSun = 8,
            colorHex = "#327EFF",
            ringColorHex = "#2259ACFF",
            radiusKm = 24622.0,
            hasRings = true,
            moonsCount = 16,
            atmosphereProperties = AtmosphereProperties(
                mainComponents = listOf(
                    GasComponent("H2", "Hydrogen", 80.0),
                    GasComponent("He", "Helium", 19.0),
                    GasComponent("CH4", "Methane", 1.5)
                ),
                densityDescription = "Highly dynamic ice giant sphere with supersonic wind currents.",
                temperatureProfile = "Coldest ambient clouds, averaging -214°C, driven down by massive winds.",
                visualAtmosphereColor = "#4D1D4ED8",
                uniqueFeature = "Blowing supersonic gale winds that reach up to 2,100 km/h - faster than speed of sound!",
                ozoneLayerPresence = false,
                atmosphericPressureBar = 1000.0
            ),
            orbitData = SimulatedOrbitData(
                semiMajorAxisAU = 30.07,
                eccentricity = 0.0086,
                orbitalPeriodDays = 60189.0,
                meanVelocityKms = 5.43,
                axialTiltDegrees = 28.32,
                inclinationDegrees = 1.77
            ),
            kidsProfile = KidsProfile(
                nickname = "The Supersonic Wind King 🌬️🔵",
                cartoonQuote = "I have the fastest, most howling winds in the solar system, zooming past planes!",
                funFact = "I am a beautiful deep royal blue planet. My weather is wild! Sometimes I have dark storms that come and go, with clouds of methane ice crystals sliding high above! 🪁🌪️",
                simpleAtmosphere = "Hydrogen and Helium with methane ice clouds whipping around.",
                gravityComparison = "Slightly stronger than Earth! You would feel slightly heavier walking here.",
                miniActivity = "My gusting winds flow at 2,100 km/h! Close your eyes and listen to the 'whoosh' of space winds!"
            ),
            adultsProfile = AdultsProfile(
                chemistryFormulas = "H₂, He, CH₄",
                technicalSummary = "Neptune exhibits deep methane absorption in its outer atmosphere, producing distinct indigo-blue scattering spectra. Meteorologic structures feature supersonic zonal wind currents up to Mach 1.7, driven by an internal heat energy source that Uranus strangely lacks, despite Uranus's identical ice giant size classification.",
                discoveryRecord = "Predicted mathematically by Urbain Le Verrier, seen by Johann Galle.",
                discoveryYear = "1846",
                significantMissions = listOf("Voyager 2 (1989 flyby)"),
                escapeVelocityKms = 23.5,
                densityGcm3 = 1.638,
                massMultiplier = 17.15
            )
        )
    )

    fun getPlanetById(id: String): Planet? = Planets.find { it.id.lowercase() == id.lowercase() }
}
