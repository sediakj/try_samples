package sample

@Grapes([
	@Grab("org.drools:drools-core:5.4.0.Beta1"),
	@Grab("org.drools:drools-compiler:5.4.0.Beta1"),
	@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
])
import org.drools.KnowledgeBaseFactory
import org.drools.runtime.conf.ClockTypeOption
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

class Data {
	String name
	int point
}

def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

builder.add(ResourceFactory.newClassPathResource(args[0], getClass()), ResourceType.DRL)

if (builder.hasErrors()) {
	println builder.errors
}

def base = KnowledgeBaseFactory.newKnowledgeBase()
base.addKnowledgePackages(builder.getKnowledgePackages())

/*
def config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
config.setOption(ClockTypeOption.get("realtime"))

def session = base.newStatefulKnowledgeSession(config, null)
*/
def session = base.newStatefulKnowledgeSession()

(0..<10).each {
	def d = new Data(name: "data${it}", point: Math.random() * 10)
	println("data : ${d.name}, ${d.point}")

	session.insert(d)

	session.fireAllRules()
	Thread.sleep(1000)
}

session.dispose()
