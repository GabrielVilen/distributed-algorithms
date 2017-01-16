package de.tuberlin.cit.vs;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CamelMain {
    private static Processor voteFactory = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            String[] parts = exchange.getIn().getBody(String.class).split("\\-");
            String voterId = parts[0];
            Boolean vote = parts[1].equalsIgnoreCase("yes");
            exchange.getIn().setBody(new Vote(voterId, vote));
        }
    };

    public static class VoteFilter {
        public boolean isYesVote(Vote vote) {
            return vote.getVote();
        }
    }

    public static class CountingAggregation implements AggregationStrategy {
        private int count = 0;

        @Override
        public Exchange aggregate(Exchange exchange, Exchange exchange1) {
            count++;
            exchange1.getIn().setBody(count);
            return exchange1;
        }
    }

    public static void main(String[] args) throws Exception {
        DefaultCamelContext ctxt = new DefaultCamelContext();
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
        activeMQComponent.setTrustAllPackages(true);
        ctxt.addComponent("activemq", activeMQComponent);

        RouteBuilder route = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:votes?noop=true")
                    .split(body().tokenize("\n"))
                    .process(voteFactory)
                    .to("activemq:queue:validationIn");

                VoteFilter voteFilter = new VoteFilter();

                from("activemq:queue:validationOut")
                    .choice()
                        .when(header("validated"))
                            .filter(method(voteFilter, "isYesVote"))
                            .aggregate(constant(0), new CountingAggregation()).completionInterval(5)
                            .to("stream:out")
                            .end()
                        .endChoice().otherwise()
                            .to("stream:err");
            }
        };

        ctxt.addRoutes(route);

        ctxt.start();
        System.in.read();
        ctxt.stop();
    }
}
