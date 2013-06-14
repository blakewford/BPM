filename_root = $0[0, $0.length-3]
filename = "./jni/"+filename_root+".h"
file = File.new(filename, "r")
line = file.gets
while (line !=nil && line != "/*Start Opcodes*/\n")
    line = file.gets
end
puts "/** Automatically generated file. DO NOT MODIFY */\npackage org.starlo.bytepusher;\npublic enum "+filename_root.capitalize()+" {"
line = file.gets
while (line !=nil && line != "/*End Opcodes*/\n")
    nextline = file.gets
    if(nextline != "/*End Opcodes*/\n")
	line = line[0, line.length-2]+","
	puts line.upcase
    else
	puts line.upcase
    end
    line = nextline
end
file.close
puts "}"
