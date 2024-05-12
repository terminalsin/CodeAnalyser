'use client'
import { useState, useEffect } from "react";
import { Review } from "./NodeDetails";
import { createAsyncToken } from "@/utils/supabase/client";

interface StatProps {
    score: number,
    code: string,
    fileName: string,
    results: Review[]
}

export default function AveragePage() {
    const [stat, setStat] = useState<StatProps[]>([]);
    const [average, setAverage] = useState(0)

    useEffect(() => {
        const fetchData = async () => {
            const token = await createAsyncToken();
            const response = await fetch('http://localhost:8080/my-data', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });

            if (response.ok) {
                const result = await response.json();  // Assuming the response is JSON formatted
                console.log(result);
                setStat(result);
                let averageScore = 0
                for (let i = 0; i < result.length; i++) {
                    averageScore += result[i].score
                }

                averageScore = averageScore / result.length
                setAverage(averageScore)
            } else {
                console.log("Failed to fetch data from the server.");
            }
        }
        fetchData();
    }, []);
return (
<h1 className={`flex grow justify-center rounded-full text-center mt-28 text-3xl font-bold bg-blue-800 w-1/2 py-1 ${average < 50 ?
    'text-red-600' : average < 80 ?
        'text-yellow-300' : 'text-lime-400'
    }`}> {Math.round(average)}/100</h1>)
}