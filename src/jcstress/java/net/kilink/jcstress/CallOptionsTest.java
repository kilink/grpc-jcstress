package net.kilink.jcstress;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;

import static org.openjdk.jcstress.annotations.Expect.*;

import io.grpc.CallOptions;
import org.openjdk.jcstress.infra.results.IIL_Result;

public class CallOptionsTest {
    @JCStressTest
    @Outcome(id = "-1, -1, not visible", expect = ACCEPTABLE, desc = "Object is not seen yet.")
    @Outcome(id = "5, 5, authority",     expect = ACCEPTABLE, desc = "Seen the complete object.")
    @Outcome(expect = FORBIDDEN,  desc = "Everything else is forbidden.")
    @State
    public static class PlainInit {
        CallOptions callOptions;

        @Actor
        public void actor1() {
            callOptions = CallOptions.DEFAULT
                    .withMaxInboundMessageSize(5)
                    .withMaxOutboundMessageSize(5)
                    .withAuthority("authority");
        }

        @Actor
        public void actor2(IIL_Result r) {
            CallOptions o = this.callOptions;
            if (o != null) {
                Integer inbound = o.getMaxInboundMessageSize();
                Integer outbound = o.getMaxOutboundMessageSize();
                String authority = o.getAuthority();
                r.r1 = inbound != null ? inbound : -2;
                r.r2 = outbound != null ? outbound : -2;
                r.r3 = authority;
            } else {
                r.r1 = -1;
                r.r2 = -1;
                r.r3 = "not visible";
            }
        }
    }
}

