'use client'

import NodeDetails, { Review } from './NodeDetails';

const reviews : Review[] = [
    {
        "code": "package dev.ca rtestsis.test.invariantloopbasic;",
        "highlight": "Good code",
        "type": "INFO"
    },
    {
        "code": "\n\nimport net.cartestsis.test.api.Sinner;\n\npublic class InvariantLoopBasicSin implements Sinner {\n\n    private static final int MAX_ITERATIONS = 10000;\n    private int complexState = 0;\n\n    public static void main(String[] args) {\n        Sinner.run(new InvariantLoopBasicSin(), args);\n    }\n\n    @Override\n    public void run(String[] args) {\n        // Initialization to avoid compiler optimizations regarding constants\n        int limit = args.length > 0 ? Integer.parseInt(args[0]) : MAX_ITERATIONS;\n        complexState = limit % 10; // Arbitrary calculation to involve input in state\n\n        // Invariant loop\n        for (int i = 0; i < limit; i++) {\n            doComplexCalculation();\n            if (shouldTerminate(i)) {\n                break;\n            }\n        }\n\n        System.out.println(\"Loop completed. Final state: \" + complexState);\n    }\n\n    // Simulate a complex calculation that changes the state\n    private void doComplexCalculation() {\n        complexState = (complexState * 3 + 1) % 101; // Some pseudo-random changes\n    }\n\n    // Check if the loop should terminate based on complex conditions\n    private boolean shouldTerminate(int iteration) {\n        // Example: terminate if the complex state is a specific value\n        return (complexState == 50 || iteration == MAX_ITERATIONS - 1);\n    }\n}",
        "highlight": "Error",
        "type": "ERROR"
    }
];

interface NodeProps {
    code: string;
    reviews: Review[];
    score: number;
}

const ContentNode: React.FC<NodeProps> = (props: NodeProps) => {
    return (
        <div className="container mx-auto px-4">
            <h1 className="text-xl text-white font-bold my-4">Node Details</h1>
            <NodeDetails code={props.code} reviews={props.reviews} score={props.score} />
        </div>
    );
};

export default ContentNode;
