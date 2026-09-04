gamerule minecraft:advance_time false
time set day
fill -1 64 -2 5 72 6 minecraft:air
fill -1 63 -2 5 63 6 minecraft:red_concrete
setblock 2 64 0 minecraft:oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:["#1", "Shift + look at", "hive: 3/3, 5/5", ""]}}
execute positioned 0 64 0 run setblock ~2 ~ ~2 minecraft:beehive[facing=north,honey_level=5]{bees:[{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000},{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000},{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000}]}
fill -10 64 -2 -4 72 6 minecraft:air
fill -10 63 -2 -4 63 6 minecraft:lime_concrete
setblock -7 64 0 minecraft:oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:["#2", "Shift+click w/", "shears: 1 bee", "out"]}}
execute positioned -9 64 0 run setblock ~2 ~ ~2 minecraft:beehive[facing=north,honey_level=5]{bees:[{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000},{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000},{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000}]}
fill -19 64 -2 -13 72 6 minecraft:air
fill -19 63 -2 -13 63 6 minecraft:light_blue_concrete
setblock -16 64 0 minecraft:oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:["#3", "Shift+click", "empty hive:", "click + smoke"]}}
setblock -16 64 2 minecraft:beehive[facing=north,honey_level=0]
fill -30 64 -2 -22 72 8 minecraft:air
fill -30 63 -2 -22 63 8 minecraft:yellow_concrete
setblock -26 64 0 minecraft:oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:["#4", "Full hive:", "honey on all 4", "sides"]}}
setblock -28 65 3 minecraft:beehive[facing=north,honey_level=5]
setblock -26 65 3 minecraft:beehive[facing=north,honey_level=3]
setblock -24 65 3 minecraft:bee_nest[facing=north,honey_level=5]
fill -39 64 -2 -33 72 6 minecraft:air
fill -39 63 -2 -33 63 6 minecraft:magenta_concrete
setblock -36 64 0 minecraft:oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:["#5", "Night:", "shift+click", "still ejects"]}}
execute positioned -38 64 0 run setblock ~2 ~ ~2 minecraft:bee_nest[facing=north,honey_level=2]{bees:[{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000},{entity_data:{id:"minecraft:bee"},ticks_in_hive:0,min_ticks_in_hive:100000}]}
setworldspawn 0 64 0
