'use client'; // This directive explicitly tells Next.js that this file is for client-side execution.

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { createAsyncToken, createClient, createStompClient } from '@/utils/supabase/client';
import { SupabaseClient, User } from '@supabase/supabase-js';
import { useRouter } from 'next/router';
import { Button, Modal, ModalFooter } from 'flowbite-react';
import { FaRegArrowAltCircleDown } from 'react-icons/fa';
import { CircularProgress, Step, StepIcon, StepLabel, Stepper, Typography, circularProgressClasses } from '@mui/material';
import { AnimatePresence, motion } from 'framer-motion';
import React from 'react';

interface StepData {
    id: string;
    icon: React.ReactNode;
    text: string;
    failedMessage: string;
    status: "none" | "success" | "failed" | "pending";
}

export default function TestButton() {
    const [user, setUser] = useState<User | null>(null);
    const [codeSnippet, setCodeSnippet] = useState('');
    const [agentResponse, setAgentResponse] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);
    const [steps, setSteps] = useState<StepData[]>([
        {
            id: "connecting",
            icon: <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h3a3 3 0 0 0 0-6h-.025a5.56 5.56 0 0 0 .025-.5A5.5 5.5 0 0 0 7.207 9.021C7.137 9.017 7.071 9 7 9a4 4 0 1 0 0 8h2.167M12 19v-9m0 0-2 2m2-2 2 2"/>
                </svg>
            ,
            text: "Connecting...",
            failedMessage: "Connection Failed",
            status: "none",
        },
        {
            id: "processing",
            icon: <svg xmlns="http://www.w3.org/2000/svg" width="0.99em" height="1em" viewBox="0 0 256 260"><path d="M239.184 106.203a64.716 64.716 0 0 0-5.576-53.103C219.452 28.459 191 15.784 163.213 21.74A65.586 65.586 0 0 0 52.096 45.22a64.716 64.716 0 0 0-43.23 31.36c-14.31 24.602-11.061 55.634 8.033 76.74a64.665 64.665 0 0 0 5.525 53.102c14.174 24.65 42.644 37.324 70.446 31.36a64.72 64.72 0 0 0 48.754 21.744c28.481.025 53.714-18.361 62.414-45.481a64.767 64.767 0 0 0 43.229-31.36c14.137-24.558 10.875-55.423-8.083-76.483m-97.56 136.338a48.397 48.397 0 0 1-31.105-11.255l1.535-.87l51.67-29.825a8.595 8.595 0 0 0 4.247-7.367v-72.85l21.845 12.636c.218.111.37.32.409.563v60.367c-.056 26.818-21.783 48.545-48.601 48.601M37.158 197.93a48.345 48.345 0 0 1-5.781-32.589l1.534.921l51.722 29.826a8.339 8.339 0 0 0 8.441 0l63.181-36.425v25.221a.87.87 0 0 1-.358.665l-52.335 30.184c-23.257 13.398-52.97 5.431-66.404-17.803M23.549 85.38a48.499 48.499 0 0 1 25.58-21.333v61.39a8.288 8.288 0 0 0 4.195 7.316l62.874 36.272l-21.845 12.636a.819.819 0 0 1-.767 0L41.353 151.53c-23.211-13.454-31.171-43.144-17.804-66.405zm179.466 41.695l-63.08-36.63L161.73 77.86a.819.819 0 0 1 .768 0l52.233 30.184a48.6 48.6 0 0 1-7.316 87.635v-61.391a8.544 8.544 0 0 0-4.4-7.213m21.742-32.69l-1.535-.922l-51.619-30.081a8.39 8.39 0 0 0-8.492 0L99.98 99.808V74.587a.716.716 0 0 1 .307-.665l52.233-30.133a48.652 48.652 0 0 1 72.236 50.391zM88.061 139.097l-21.845-12.585a.87.87 0 0 1-.41-.614V65.685a48.652 48.652 0 0 1 79.757-37.346l-1.535.87l-51.67 29.825a8.595 8.595 0 0 0-4.246 7.367zm11.868-25.58L128.067 97.3l28.188 16.218v32.434l-28.086 16.218l-28.188-16.218z"/></svg>,
            text: "Processing...",
            failedMessage: "Failed to process code",
            status: "none",
        },
        {
            id: "openai",
            icon: <svg xmlns="http://www.w3.org/2000/svg" width="0.99em" height="1em" viewBox="0 0 256 260"><path d="M239.184 106.203a64.716 64.716 0 0 0-5.576-53.103C219.452 28.459 191 15.784 163.213 21.74A65.586 65.586 0 0 0 52.096 45.22a64.716 64.716 0 0 0-43.23 31.36c-14.31 24.602-11.061 55.634 8.033 76.74a64.665 64.665 0 0 0 5.525 53.102c14.174 24.65 42.644 37.324 70.446 31.36a64.72 64.72 0 0 0 48.754 21.744c28.481.025 53.714-18.361 62.414-45.481a64.767 64.767 0 0 0 43.229-31.36c14.137-24.558 10.875-55.423-8.083-76.483m-97.56 136.338a48.397 48.397 0 0 1-31.105-11.255l1.535-.87l51.67-29.825a8.595 8.595 0 0 0 4.247-7.367v-72.85l21.845 12.636c.218.111.37.32.409.563v60.367c-.056 26.818-21.783 48.545-48.601 48.601M37.158 197.93a48.345 48.345 0 0 1-5.781-32.589l1.534.921l51.722 29.826a8.339 8.339 0 0 0 8.441 0l63.181-36.425v25.221a.87.87 0 0 1-.358.665l-52.335 30.184c-23.257 13.398-52.97 5.431-66.404-17.803M23.549 85.38a48.499 48.499 0 0 1 25.58-21.333v61.39a8.288 8.288 0 0 0 4.195 7.316l62.874 36.272l-21.845 12.636a.819.819 0 0 1-.767 0L41.353 151.53c-23.211-13.454-31.171-43.144-17.804-66.405zm179.466 41.695l-63.08-36.63L161.73 77.86a.819.819 0 0 1 .768 0l52.233 30.184a48.6 48.6 0 0 1-7.316 87.635v-61.391a8.544 8.544 0 0 0-4.4-7.213m21.742-32.69l-1.535-.922l-51.619-30.081a8.39 8.39 0 0 0-8.492 0L99.98 99.808V74.587a.716.716 0 0 1 .307-.665l52.233-30.133a48.652 48.652 0 0 1 72.236 50.391zM88.061 139.097l-21.845-12.585a.87.87 0 0 1-.41-.614V65.685a48.652 48.652 0 0 1 79.757-37.346l-1.535.87l-51.67 29.825a8.595 8.595 0 0 0-4.246 7.367zm11.868-25.58L128.067 97.3l28.188 16.218v32.434l-28.086 16.218l-28.188-16.218z"/></svg>,
            text: "Connecting to OpenAI",
            failedMessage: "Failed to connect to OpenAI",
            status: "none",
        },
        {
            id: "llama",
            icon: <svg xmlns="http://www.w3.org/2000/svg" width="0.99em" height="1em" viewBox="0 0 256 260"><path d="M239.184 106.203a64.716 64.716 0 0 0-5.576-53.103C219.452 28.459 191 15.784 163.213 21.74A65.586 65.586 0 0 0 52.096 45.22a64.716 64.716 0 0 0-43.23 31.36c-14.31 24.602-11.061 55.634 8.033 76.74a64.665 64.665 0 0 0 5.525 53.102c14.174 24.65 42.644 37.324 70.446 31.36a64.72 64.72 0 0 0 48.754 21.744c28.481.025 53.714-18.361 62.414-45.481a64.767 64.767 0 0 0 43.229-31.36c14.137-24.558 10.875-55.423-8.083-76.483m-97.56 136.338a48.397 48.397 0 0 1-31.105-11.255l1.535-.87l51.67-29.825a8.595 8.595 0 0 0 4.247-7.367v-72.85l21.845 12.636c.218.111.37.32.409.563v60.367c-.056 26.818-21.783 48.545-48.601 48.601M37.158 197.93a48.345 48.345 0 0 1-5.781-32.589l1.534.921l51.722 29.826a8.339 8.339 0 0 0 8.441 0l63.181-36.425v25.221a.87.87 0 0 1-.358.665l-52.335 30.184c-23.257 13.398-52.97 5.431-66.404-17.803M23.549 85.38a48.499 48.499 0 0 1 25.58-21.333v61.39a8.288 8.288 0 0 0 4.195 7.316l62.874 36.272l-21.845 12.636a.819.819 0 0 1-.767 0L41.353 151.53c-23.211-13.454-31.171-43.144-17.804-66.405zm179.466 41.695l-63.08-36.63L161.73 77.86a.819.819 0 0 1 .768 0l52.233 30.184a48.6 48.6 0 0 1-7.316 87.635v-61.391a8.544 8.544 0 0 0-4.4-7.213m21.742-32.69l-1.535-.922l-51.619-30.081a8.39 8.39 0 0 0-8.492 0L99.98 99.808V74.587a.716.716 0 0 1 .307-.665l52.233-30.133a48.652 48.652 0 0 1 72.236 50.391zM88.061 139.097l-21.845-12.585a.87.87 0 0 1-.41-.614V65.685a48.652 48.652 0 0 1 79.757-37.346l-1.535.87l-51.67 29.825a8.595 8.595 0 0 0-4.246 7.367zm11.868-25.58L128.067 97.3l28.188 16.218v32.434l-28.086 16.218l-28.188-16.218z"/></svg>,
            text: "Connecting to Llama",
            failedMessage: "Failed to connect to Llama",
            status: "none",
        },
        {
            id: "storage",
            icon: <svg xmlns="http://www.w3.org/2000/svg" width="0.99em" height="1em" viewBox="0 0 256 260"><path d="M239.184 106.203a64.716 64.716 0 0 0-5.576-53.103C219.452 28.459 191 15.784 163.213 21.74A65.586 65.586 0 0 0 52.096 45.22a64.716 64.716 0 0 0-43.23 31.36c-14.31 24.602-11.061 55.634 8.033 76.74a64.665 64.665 0 0 0 5.525 53.102c14.174 24.65 42.644 37.324 70.446 31.36a64.72 64.72 0 0 0 48.754 21.744c28.481.025 53.714-18.361 62.414-45.481a64.767 64.767 0 0 0 43.229-31.36c14.137-24.558 10.875-55.423-8.083-76.483m-97.56 136.338a48.397 48.397 0 0 1-31.105-11.255l1.535-.87l51.67-29.825a8.595 8.595 0 0 0 4.247-7.367v-72.85l21.845 12.636c.218.111.37.32.409.563v60.367c-.056 26.818-21.783 48.545-48.601 48.601M37.158 197.93a48.345 48.345 0 0 1-5.781-32.589l1.534.921l51.722 29.826a8.339 8.339 0 0 0 8.441 0l63.181-36.425v25.221a.87.87 0 0 1-.358.665l-52.335 30.184c-23.257 13.398-52.97 5.431-66.404-17.803M23.549 85.38a48.499 48.499 0 0 1 25.58-21.333v61.39a8.288 8.288 0 0 0 4.195 7.316l62.874 36.272l-21.845 12.636a.819.819 0 0 1-.767 0L41.353 151.53c-23.211-13.454-31.171-43.144-17.804-66.405zm179.466 41.695l-63.08-36.63L161.73 77.86a.819.819 0 0 1 .768 0l52.233 30.184a48.6 48.6 0 0 1-7.316 87.635v-61.391a8.544 8.544 0 0 0-4.4-7.213m21.742-32.69l-1.535-.922l-51.619-30.081a8.39 8.39 0 0 0-8.492 0L99.98 99.808V74.587a.716.716 0 0 1 .307-.665l52.233-30.133a48.652 48.652 0 0 1 72.236 50.391zM88.061 139.097l-21.845-12.585a.87.87 0 0 1-.41-.614V65.685a48.652 48.652 0 0 1 79.757-37.346l-1.535.87l-51.67 29.825a8.595 8.595 0 0 0-4.246 7.367zm11.868-25.58L128.067 97.3l28.188 16.218v32.434l-28.086 16.218l-28.188-16.218z"/></svg>,
            text: "Storing data...",
            failedMessage: "Failed to store result to backend",
            status: "none",
        },
        {
            id: "completed",
            icon: <svg xmlns="http://www.w3.org/2000/svg" width="0.99em" height="1em" viewBox="0 0 256 260"><path d="M239.184 106.203a64.716 64.716 0 0 0-5.576-53.103C219.452 28.459 191 15.784 163.213 21.74A65.586 65.586 0 0 0 52.096 45.22a64.716 64.716 0 0 0-43.23 31.36c-14.31 24.602-11.061 55.634 8.033 76.74a64.665 64.665 0 0 0 5.525 53.102c14.174 24.65 42.644 37.324 70.446 31.36a64.72 64.72 0 0 0 48.754 21.744c28.481.025 53.714-18.361 62.414-45.481a64.767 64.767 0 0 0 43.229-31.36c14.137-24.558 10.875-55.423-8.083-76.483m-97.56 136.338a48.397 48.397 0 0 1-31.105-11.255l1.535-.87l51.67-29.825a8.595 8.595 0 0 0 4.247-7.367v-72.85l21.845 12.636c.218.111.37.32.409.563v60.367c-.056 26.818-21.783 48.545-48.601 48.601M37.158 197.93a48.345 48.345 0 0 1-5.781-32.589l1.534.921l51.722 29.826a8.339 8.339 0 0 0 8.441 0l63.181-36.425v25.221a.87.87 0 0 1-.358.665l-52.335 30.184c-23.257 13.398-52.97 5.431-66.404-17.803M23.549 85.38a48.499 48.499 0 0 1 25.58-21.333v61.39a8.288 8.288 0 0 0 4.195 7.316l62.874 36.272l-21.845 12.636a.819.819 0 0 1-.767 0L41.353 151.53c-23.211-13.454-31.171-43.144-17.804-66.405zm179.466 41.695l-63.08-36.63L161.73 77.86a.819.819 0 0 1 .768 0l52.233 30.184a48.6 48.6 0 0 1-7.316 87.635v-61.391a8.544 8.544 0 0 0-4.4-7.213m21.742-32.69l-1.535-.922l-51.619-30.081a8.39 8.39 0 0 0-8.492 0L99.98 99.808V74.587a.716.716 0 0 1 .307-.665l52.233-30.133a48.652 48.652 0 0 1 72.236 50.391zM88.061 139.097l-21.845-12.585a.87.87 0 0 1-.41-.614V65.685a48.652 48.652 0 0 1 79.757-37.346l-1.535.87l-51.67 29.825a8.595 8.595 0 0 0-4.246 7.367zm11.868-25.58L128.067 97.3l28.188 16.218v32.434l-28.086 16.218l-28.188-16.218z"/></svg>,
            text: "Output generated!",
            failedMessage: "Failed to output code",
            status: "none",
        }
    ]);
    const [currentStep, setCurrentStep] = useState(0);

    useEffect(() => {
        const supabase: SupabaseClient = createClient();
        const fetchUser = async () => {
            const { data: { session } } = await supabase.auth.getSession();
            setUser(session?.user ?? null);
        };

        fetchUser();
    }, []);    

    const isStepFailed = (step: StepData) => {
        return step.status === "failed";
      };

      const clear = () => {
        steps.forEach((step) => {
            step.status = "none";
        });
        setCurrentStep(0);
        setIsSuccess(false);
      }

    const handleTestGenerate = async () => {
        if (!codeSnippet) {
            setAgentResponse("Please enter a code snippet to analyze.");
            return;
        }

        clear();
        setIsLoading(true);
        const client = await createClient();
        const token = await createAsyncToken();

        const rxStomp = await createStompClient();
        const userId = (await client.auth.getUser()).data.user?.id;

        const subscription = rxStomp.watch('/user/queue/review/status').subscribe(async (message) => {
            console.log('Received message: ', message);

            const deserialized = JSON.parse(message.body);
            switch (deserialized.status) {
                case 'connected':
                    const response = await fetch('http://localhost:8080/generate', {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({ code: codeSnippet })
                    });

                    if (response.ok) {
                        const result = await response.json();  // Assuming the response is JSON formatted
                        setAgentResponse(JSON.stringify(result, null, 2));
                    } else {
                        setAgentResponse("Failed to fetch data from the server.");
                    }
                    break;
                case 'completed':
                    subscription.unsubscribe();
                    setIsSuccess(true);
                    setCurrentStep(steps.length - 1);
                    steps[steps.length - 1].status = 'success';
                    break;
                default:
                    // Assign it to the correct step:
                    // if I receive connecting:start, set connecting to true
                    // if I receive connecting:failed, set failure to true
                    // same goes for openai etc

                    const step = deserialized.status.split(':')[0];
                    const status = deserialized.status.split(':')[1];

                    const index = steps.findIndex((s) => s.id === step);

                    if (index === -1) {
                        console.log('Step not found:', step);
                    }

                    switch (status) {
                        case 'start':
                            steps[index].status = 'pending';
                            break;
                        case 'failed':
                            steps[index].status = 'failed';
                            break;
                        case 'completed':
                            steps[index].status = 'success';
                            break;
                    }

                    // if receive completed status, set the current step to the next step
                    if (status === 'completed') {
                        setCurrentStep(currentStep + 1);
                    }
                    break;
            }
        });
    };
    
    const MotionStep = motion(Step);

    return user ? (
        <div className="w-4/5">
            <textarea
                placeholder="Enter your Java code snippet here..."
                value={codeSnippet}
                onChange={(e) => setCodeSnippet(e.target.value)}
                className="mb-4 w-full h-48 p-2 border border-gray-300 rounded-md text-black"
            />
            <motion.button 
                onClick={handleTestGenerate} 
                disabled={isLoading} 
                variants={{
                    hover: { scale: 1.1 },
                    tap: { scale: 0.95 }
                }}
                whileHover="hover"
                whileTap="tap"
                className="py-2 px-4 rounded-md no-underline bg-blue-500 hover:bg-blue-600 text-white disabled:bg-gray-400"
            >
                {isLoading ? 'Analyzing...' : 'Analyze Code'}
            </motion.button>
            <AnimatePresence>
                {isLoading && (
                    <motion.div
                        initial="hidden"
                        animate="visible"
                        exit="hidden"
                        variants={{
                            hidden: { opacity: 0, y: 0, transition: { duration: 0.5 } },
                            visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
                        }}
                        transition={{ duration: 0.5 }}
                        className="fixed inset-0 bg-gray-500 bg-opacity-75"
                    >
                        
                        <Modal show={isLoading} size="5xl" dismissible={isSuccess} onClose={() => setIsLoading(false)}>
                        <motion.div
                            variants={{
                                hidden: { opacity: 0, y: 0, transition: { duration: 0.5 } },
                                visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
                            }}
                            initial="hidden"
                            animate="visible"
                            exit="hidden"
                            className=""
                        >
                            <Modal.Body>
                            <motion.h2 className="text-lg font-bold justify-center text-center text-black py-5 pb-5">Analyzing code snippet...</motion.h2>
                            <Stepper alternativeLabel activeStep={currentStep}>
                                
                                {steps
                                    .filter((step, index) => steps[index].status !== "none")
                                    .map((step, index) => {
                                    const labelProps: {
                                        optional?: React.ReactNode;
                                        error?: boolean;
                                    } = {};
                                    if (isStepFailed(step)) {
                                        labelProps.optional = (
                                        <Typography variant="caption" color="error">
                                            {step.failedMessage}
                                        </Typography>
                                        );
                                        labelProps.error = true;
                                    }

                                    // Spinner if step is pending
                                    const iconProps: {
                                        icon?: React.ReactNode;
                                    } = {};
                                    
                                        if (step.status === "pending") {
                                            iconProps.icon =  <React.Fragment>
                                            <svg width={0} height={0}>
                                              <defs>
                                                <linearGradient id="my_gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                                                  <stop offset="0%" stopColor="#e01cd5" />
                                                  <stop offset="100%" stopColor="#1CB5E0" />
                                                </linearGradient>
                                              </defs>
                                            </svg>
                                            <CircularProgress size={30} sx={{ 'svg circle': { stroke: 'url(#my_gradient)' } }} />
                                          </React.Fragment>
                                        }

                                    console.log("Step: ", step, " props: ", iconProps);
                            
                                    return (
                                            <MotionStep 
                                                variants={{
                                                    hidden: { opacity: 0, y: -20, transition: { duration: 0.5 } },
                                                    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
                                                }}
                                                initial="hidden"
                                                animate="visible"
                                                exit="hidden"
                                                key={index}  expanded={index == 0}>
                                                        <StepLabel {...iconProps} {...labelProps} >{step.text}</StepLabel>
                                            </MotionStep>
                                    );
                                })}
                            </Stepper>

                            </Modal.Body>
                            {isSuccess && (
                                <ModalFooter>
                                    <div className="flex grow justify-center gap-4 text-center">
                                        <Button className="w-40" color="failure" onClick={() => setIsLoading(false)}>
                                            {"Close"}
                                        </Button>
                                        </div>
                                </ModalFooter>
                                
                            )}
                            </motion.div>
                        </Modal>
                        
                </motion.div>)}
            </AnimatePresence>
            <pre className="mt-4 p-2 bg-gray-100 rounded-md text-black">{agentResponse}</pre>
        </div>
    ) : (
        <Link
            href="/login"
            className="py-2 px-3 flex rounded-md no-underline bg-blue-500 hover:bg-blue-600 text-white"
        >
            Login
        </Link>
    );
}
