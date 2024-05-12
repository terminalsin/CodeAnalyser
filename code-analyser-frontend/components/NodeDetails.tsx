    'use client'

    import { Alert, Flowbite, Tabs, Tooltip } from 'flowbite-react';
    import React, { useEffect, useRef, useState } from 'react';
    import HighlightWithinTextarea from 'react-highlight-within-textarea';
    import 'react-quill/dist/quill.snow.css';
    import { HiInformationCircle } from "react-icons/hi";

import { customTabsTheme } from './VerticalTabs';
    
    export interface Review {
        code: string;
        highlight: string;
        type: 'NONE' | 'INFO' | 'WARNING' | 'ERROR';
    }

    interface ReviewsPanelProps {
        code: string;
        reviews: Review[];
        score: number;
    }

    const ReviewsPanel: React.FC<ReviewsPanelProps> = (props: ReviewsPanelProps) => {
        const highlight = props.reviews.map(e => {
            let color: string | undefined = undefined;
            switch (e.type) {
                case 'INFO':
                    color = 'bg-blue-200 rounded-md p-1'; // light blue
                    break;
                case 'WARNING':
                    color = 'bg-yellow-200 rounded-md p-1'; // light yellow
                    break;
                case 'ERROR':
                    color = 'bg-red-200  rounded-md p-1'; // light red
                    break;
                default:
                    color = 'rounded-md p-1'; // light gray (none)
            }

            return {
                highlight: e.code.split('\n'),
                component: (props: any) => (
                    <Tooltip content={e.highlight} style="light" className='text-black' arrow={false}>
                        <mark className={color}>{props.children}</mark>
                    </Tooltip>
                ),
                className: 'blue'
            }
        });

        return (
            <div className="flex gap-10">
                <div className="flex-grow w-4/12 p-4 bg-white rounded-xl my-5 text-black h-30">
                    <HighlightWithinTextarea
                        value={props.code}
                        highlight={highlight}
                    />
                </div>
                <div className="my-5 flex-1 p-4 border-l border-gray-400 bg-white rounded-xl">
                    <h2 className="text-lg font-bold mb-4 text-black">Score: {props.score}</h2>
                    <button className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-xl">
                        Reanalyze
                    </button>
                    <h2 className="text-lg font-bold mb-4 text-black pt-4">Issues</h2>
                    <Alert color="info" icon={HiInformationCircle}>
                        There are <span className="font-medium">{props.reviews.filter(e => e.type == "INFO").length}</span> info alerts 
                    </Alert>
                    <br/>
                    <Alert color="warning" icon={HiInformationCircle}>
                        There are <span className="font-medium">{props.reviews.filter(e => e.type == "WARNING").length}</span> info alerts 
                    </Alert>
                    <br/>
                    <Alert color="failure" icon={HiInformationCircle}>
                        There are <span className="font-medium">{props.reviews.filter(e => e.type == "ERROR").length}</span> info alerts 
                    </Alert>
                    
                </div>
            </div>
        );
    };

    export default ReviewsPanel;

