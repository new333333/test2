writer = new StringWriter()
bd = new groovy.xml.MarkupBuilder(writer)
bd.html{
	head{
		title("Directory listing for ${parent.getName()}")
	}
	body{
		children.each {
			a(href:it.getName(), it.getName())
		}
	}
}

output = writer