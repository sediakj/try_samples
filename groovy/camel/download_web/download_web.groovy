
@GrabResolver(name = "Apache Camel SNAPSHOTS", root = "https://repository.apache.org/content/repositories/snapshots/")
@Grab("org.apache.camel:camel-core:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-http:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-stream:2.9.0-SNAPSHOT")
import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

def dir = args[0]

ctx = new DefaultCamelContext()

ctx.addRoutes(new RouteBuilder() {
	void configure() {
		onException(Exception.class)
			.handled(true)
			.process({
				println "failed: ${it.in.body}, ${it.exception}"
			} as Processor)

		from("stream:in")
			.split(body().tokenize())
			.setHeader(Exchange.HTTP_URI, body())
			.to("http://localhost/")
			.process({
				it.in.setHeader(Exchange.FILE_NAME, it.in.getHeader(Exchange.HTTP_URI).split('/').last())
			} as Processor)
			.to("file:${dir}")
			.process({
				println "downloaded: ${it.in.getHeader(Exchange.HTTP_URI)} => ${it.in.getHeader(Exchange.FILE_NAME)}"
			} as Processor)
	}
})

ctx.start()

Thread.sleep(1000)

ctx.stop()