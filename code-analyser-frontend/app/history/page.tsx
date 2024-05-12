'use client'

import Link from "next/link";
import { headers } from "next/headers";
import { createClient } from "@/utils/supabase/server";
import { redirect } from "next/navigation";
import RectangleComp from "@/components/RectangleComp";
import { useEffect, useState } from "react";
import { createAsyncToken, createStompClient } from "@/utils/supabase/client";
import { Review } from "@/components/NodeDetails";

interface HistoryProps {
    score: number,
    code: string,
    fileName: string,
    results: Review[]
}

export default function History(){
    const [history, setHistory] = useState<HistoryProps[]>([]);
    const [subscription, setSubscription] = useState<any>(null);
    const fetchData = async () => {
        console.log('i fire once');
        const token = await createAsyncToken();
        const response = await fetch('http://localhost:8080/my-data', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/x-www-form-urlencoded',
            },

        }).then((response) => {
            if (response.ok) {
                return response.json();  // Assuming the response is JSON formatted
            } else {
                console.log("Failed to fetch data from the server.");
                throw new Error("Failed to fetch data from the server.");
            }
        }).then((result) => {
            setHistory(result);
        });
        const rxStomp = await createStompClient();
        rxStomp.watch('/user/queue/review').subscribe(async (message) => {
            console.log(message.body);
            const newReview = JSON.parse(message.body);
            setHistory((prev) => {
                return [...prev, newReview];
            });
        });
    }

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <div className="flex flex-col w-4/5 min-h-screen">
            <div className="w-full p-[4px] bg-gray-500 rounded-full my-1" />
            <h1 className="text-center text-3xl font-bold p-10 border-full">View-Uploads</h1>
        <div className="flex flex-col bg-neutral-200 p-16 rounded-lg gap-10">

        {history.map((item, index) => (
            <RectangleComp key={index} code={item.code} score={item.score} fileName={item.fileName} results={item.results}/>
        ))}
        </div>
        
        </div>
    )
}
