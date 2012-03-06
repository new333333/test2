writer = new StringWriter()
bd = new groovy.xml.MarkupBuilder(writer)
bd.html{
	head{
		title("Directory listing for ${parent.getName()}")
	}
	body{
		children.each {
			a(href: parent.getName() + "/" + it.getName(), it.getName())
			br()
		}
	}
}

output = writer