'use client'

import React from 'react';
import { useEffect, useState } from 'react';
import { Review } from './NodeDetails';
import { createAsyncToken } from '@/utils/supabase/client';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    Colors,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    Colors
);


ChartJS.defaults.font.size = 18;

export const options = {
    indexAxis: 'y' as const,
    elements: {
        bar: {
            borderWidth: 2,
        },
    },
    layout: {
        padding: {
            top: 30,
            left: 10,
            bottom: 30,
        }
    },
    responsive: true,
    plugins: {
        legend: {
            position: 'right' as const,
        },
        title: {
            display: true,
            text: '',
        },
        colors: {
            enabled: true,
            forceOverride: true,
        }
    },
};

const labels = [
    ''
];



interface HistoryProps {
    score: number,
    code: string,
    fileName: string,
    results: Review[]
}

export default function BarGraph() {
    const [history, setHistory] = useState<HistoryProps[]>([]);
    const [GraphData1, setGraphData1] = useState(1);
    const [GraphData2, setGraphData2] = useState(2);
    const [GraphData3, setGraphData3] = useState(3);

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
                const result: HistoryProps[] = await response.json();  // Assuming the response is JSON formatted
                console.log(result);
                // setHistory(result);
                let contArr = [0, 0, 0]
                for (let prop of result) {
                    contArr[0] = prop.results.filter(e => e.type == "INFO").length
                    contArr[1] = prop.results.filter(e => e.type == "WARNING").length
                    contArr[2] = prop.results.filter(e => e.type == "ERROR").length
                }
                setGraphData1(contArr[0])
                setGraphData2(contArr[1])
                setGraphData3(contArr[2])
            } else {
                console.log("Failed to fetch data from the server.");
            }
        }
        fetchData();
    }, []);

    const data = {
        labels,
        datasets: [
            {
                label: 'INFO',
                data: [GraphData1],

            },
            {
                label: 'ERROR',
                data: [GraphData3],
            },
            {
                label: 'WARNING',
                data: [GraphData2],

            },
        ],
    };

    return <Bar options={options} data={data} />;
}